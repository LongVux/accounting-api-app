package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.configs.audit.AuditorAwareImpl;
import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.*;
import com.outwork.accountingapiapp.models.payload.requests.*;
import com.outwork.accountingapiapp.models.payload.responses.AccountEntrySumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.GeneralAccountEntryTableItem;
import com.outwork.accountingapiapp.repositories.GeneralAccountEntryRepository;
import com.outwork.accountingapiapp.utils.DateTimeUtils;
import com.outwork.accountingapiapp.utils.GeneralAccountEntryCodeHandler;
import com.outwork.accountingapiapp.utils.Util;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class GeneralAccountEntryService {
    public static final String ERROR_MSG_INVALID_ACTION_ON_ENTRY = "Không thể thực hiện hành động này trên bút toán được chọn";
    public static final String ERROR_UNSUPPORTED_TRANSACTION_TYPE = "Không hỗ trợ loại giao dịch này";
    public static final String ENTRY_TYPE_BANK_RETURN = "Ngân hàng kết toán";
    @Autowired
    private GeneralAccountEntryRepository generalAccountEntryRepository;

    @Autowired
    private BranchAccountEntryService branchAccountEntryService;

    @Autowired
    private UserService userService;

    @Autowired
    private Util util;

    public GeneralAccountEntryEntity getEntryById (@NotNull UUID id) {
        return generalAccountEntryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<GeneralAccountEntryTableItem> getBranchAccountEntryTableItems (GetGeneralAccountEntryTableItemRequest request) {
        return generalAccountEntryRepository.findAll(request, request.retrievePageConfig()).map(GeneralAccountEntryTableItem::new);
    }

    public AccountEntrySumUpInfo getGeneralAccountEntrySumUpInfo (GetGeneralAccountEntryTableItemRequest request) {
        Map<Object, Double> queryResult = util.getGroupedSumsBySpecification(request, BranchAccountEntryEntity.FIELD_TRANSACTION_TYPE, BranchAccountEntryEntity.FIELD_MONEY_AMOUNT, GeneralAccountEntryEntity.class);

        AccountEntrySumUpInfo response = new AccountEntrySumUpInfo();

        response.setTotalIntake(Optional.ofNullable(queryResult.get(TransactionTypeEnum.INTAKE)).orElse(0d));
        response.setTotalPayout(Optional.ofNullable(queryResult.get(TransactionTypeEnum.PAYOUT)).orElse(0d));
        response.setTotalLoan(Optional.ofNullable(queryResult.get(TransactionTypeEnum.LOAN)).orElse(0d));
        response.setTotalRepayment(Optional.ofNullable(queryResult.get(TransactionTypeEnum.REPAYMENT)).orElse(0d));
        response.setTotal(response.getTotalIntake() - response.getTotalPayout() - response.getTotalLoan() + response.getTotalRepayment());

        return response;
    }

    public GeneralAccountEntryEntity saveEntry (@Valid SaveGeneralAccountEntryRequest request, UUID id) {
        GeneralAccountEntryEntity savedEntry = ObjectUtils.isEmpty(id) ? new GeneralAccountEntryEntity() : getEntryById(id);

        savedEntry.setEntryType(request.getEntryType());
        savedEntry.setMoneyAmount(request.getMoneyAmount());
        savedEntry.setTransactionType(request.getTransactionType());
        savedEntry.setExplanation(request.getExplanation());
        savedEntry.setImageId(request.getImageId());
        savedEntry.setEntryStatus(AccountEntryStatusEnum.PENDING);

        validateAccountEntryForModification(savedEntry);

        return generalAccountEntryRepository.save(savedEntry);
    }

    public void saveGeneralAccountEntryNote (SaveNoteRequest request) {
        GeneralAccountEntryEntity generalAccountEntry = getEntryById(request.getId());
        generalAccountEntry.setNote(request.getNote());

        generalAccountEntryRepository.save(generalAccountEntry);
    }

    public void generateGeneralAccountEntryFromMatchedBills(MatchingBillRequest request, List<BillEntity> matchedBills) {
        GeneralAccountEntryEntity savedEntry = new GeneralAccountEntryEntity();

        double moneyAmount = matchedBills.stream().mapToDouble(BillEntity::getReturnFromBank).sum();

        savedEntry.setEntryType(ENTRY_TYPE_BANK_RETURN);
        savedEntry.setTransactionType(moneyAmount < 0 ? TransactionTypeEnum.PAYOUT : TransactionTypeEnum.INTAKE);
        savedEntry.setEntryCode(getNewGeneralEntryCode(savedEntry));
        savedEntry.setEntryStatus(AccountEntryStatusEnum.APPROVED);
        savedEntry.setImageId(request.getImageId());

        String joinedBillCodes = String.join(DataFormat.NEW_LINE_SEPARATOR, matchedBills.stream().map(BillEntity::getCode).toList());
        savedEntry.setExplanation(String.join(DataFormat.NEW_LINE_SEPARATOR, joinedBillCodes, request.getExplanation()));
        savedEntry.setMoneyAmount(Math.abs(moneyAmount));

        UserEntity approver = AuditorAwareImpl.getUserFromSecurityContext();

        approver.setAccountBalance(approver.getAccountBalance() + savedEntry.getMoneyAmount());

        savedEntry.setRemainingBalance(approver.getAccountBalance());

        userService.saveUserEntity(approver);

        generalAccountEntryRepository.save(savedEntry);
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public GeneralAccountEntryEntity approveEntry (@NotNull UUID id) {
        GeneralAccountEntryEntity approvedEntry = getEntryById(id);

        validateAccountEntryForModification(approvedEntry);

        approvedEntry.setEntryCode(getNewGeneralEntryCode(approvedEntry));
        approvedEntry.setEntryStatus(AccountEntryStatusEnum.APPROVED);
        approvedEntry.setCreatedDate(new Date());

        Optional<UserEntity> user = userService.findUserEntityByCode(approvedEntry.getEntryType());
        UserEntity approver = AuditorAwareImpl.getUserFromSecurityContext();

        if (user.isPresent()) {
            handleApprovingEntryRelatedToUser(approvedEntry, user.get());
        } else {
            if (List.of(TransactionTypeEnum.INTAKE, TransactionTypeEnum.REPAYMENT).contains(approvedEntry.getTransactionType())) {
                approver.setAccountBalance(approver.getAccountBalance() + approvedEntry.getMoneyAmount());
            } else {
                approver.setAccountBalance(approver.getAccountBalance() - approvedEntry.getMoneyAmount());
            }
        }

        userService.saveUserEntity(approver);
        approvedEntry.setRemainingBalance(approver.getAccountBalance());

        return generalAccountEntryRepository.save(approvedEntry);
    }

    public void handleApprovingEntryRelatedToUser (GeneralAccountEntryEntity entry, UserEntity user) {
        UserEntity approver = AuditorAwareImpl.getUserFromSecurityContext();

        if (approver.getCode().equals(user.getCode())) {
            if (TransactionTypeEnum.PAYOUT.equals(entry.getTransactionType())) {
                approver.setAccountBalance(approver.getAccountBalance() - entry.getMoneyAmount());
            } else if (TransactionTypeEnum.INTAKE.equals(entry.getTransactionType())) {
                approver.setAccountBalance(approver.getAccountBalance() + entry.getMoneyAmount());
            } else {
                throw new InvalidDataException(ERROR_UNSUPPORTED_TRANSACTION_TYPE);
            }

            userService.saveUserEntity(approver);
            return;
        }

        if (TransactionTypeEnum.PAYOUT.equals(entry.getTransactionType())) {
            approver.setAccountBalance(approver.getAccountBalance() - entry.getMoneyAmount());
            user.setAccountBalance(user.getAccountBalance() + entry.getMoneyAmount());
        } else if (TransactionTypeEnum.INTAKE.equals(entry.getTransactionType())) {
            approver.setAccountBalance(approver.getAccountBalance() + entry.getMoneyAmount());
            user.setAccountBalance(user.getAccountBalance() - entry.getMoneyAmount());
        } else {
            throw new InvalidDataException(ERROR_UNSUPPORTED_TRANSACTION_TYPE);
        }

        userService.saveUserEntity(approver);
        userService.saveUserEntity(user);
    }

    public void deleteBranchAccountEntry (@NotNull UUID id) {
        GeneralAccountEntryEntity entry = getEntryById(id);

        validateAccountEntryForModification(entry);

        generalAccountEntryRepository.deleteById(id);
    }

    private void validateAccountEntryForModification(GeneralAccountEntryEntity entry) {
        if (!ObjectUtils.isEmpty(entry.getEntryCode())) {
            throw new InvalidDataException(ERROR_MSG_INVALID_ACTION_ON_ENTRY);
        }
    }

    private String getNewGeneralEntryCode (GeneralAccountEntryEntity entry) {
        Optional<GeneralAccountEntryEntity> latestEntry = generalAccountEntryRepository.findFirstByEntryCodeNotNullAndTransactionTypeAndCreatedDateBetweenOrderByCreatedDateDesc(
                entry.getTransactionType(),
                DateTimeUtils.atStartOfDay(new Date()),
                DateTimeUtils.atEndOfDay(new Date())
        );

        return GeneralAccountEntryCodeHandler.generateAccountEntryCode(
                entry.getTransactionType(),
                latestEntry.map(GeneralAccountEntryEntity::getEntryCode).orElse(null)
        );
    }
}
