package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.StringConstant;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.GeneralAccountEntryEntity;
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

import java.util.*;

@Service
public class GeneralAccountEntryService {
    public static final String ERROR_MSG_INVALID_ACTION_ON_ENTRY = "Không thể thực hiện hành động này trên bút toán được chọn";
    public static final String ERROR_UNSUPPORTED_TRANSACTION_TYPE = "Không hỗ trợ loại giao dịch này";

    @Autowired
    private GeneralAccountEntryRepository generalAccountEntryRepository;

    @Autowired
    private BranchAccountEntryService branchAccountEntryService;

    @Autowired
    private BranchService branchService;

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

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public GeneralAccountEntryEntity approveEntry (@NotNull UUID id) {
        GeneralAccountEntryEntity approvedEntry = getEntryById(id);

        validateAccountEntryForModification(approvedEntry);

        approvedEntry.setEntryCode(getNewGeneralEntryCode(approvedEntry));
        approvedEntry.setEntryStatus(AccountEntryStatusEnum.APPROVED);
        approvedEntry.setCreatedDate(new Date());

        branchService.getBranchByCode(approvedEntry.getEntryType())
                .ifPresent(branchEntity -> handleApprovingEntryRelatedToBranch(approvedEntry, branchEntity));

        return generalAccountEntryRepository.save(approvedEntry);
    }

    public void handleApprovingEntryRelatedToBranch (GeneralAccountEntryEntity entry, BranchEntity branch) {
        SaveBranchAccountEntryRequest request = new SaveBranchAccountEntryRequest();

        if (TransactionTypeEnum.PAYOUT.equals(entry.getTransactionType())) {
            request.setTransactionType(TransactionTypeEnum.INTAKE);
            request.setEntryType(StringConstant.ENTRY_TYPE_ADVANCED_PAYOUT);
        } else if (TransactionTypeEnum.INTAKE.equals(entry.getTransactionType())) {
            request.setTransactionType(TransactionTypeEnum.PAYOUT);
            request.setEntryType(StringConstant.ENTRY_TYPE_REPAYMENT);
        } else {
            throw new InvalidDataException(ERROR_UNSUPPORTED_TRANSACTION_TYPE);
        }

        request.setBranchId(branch.getId());
        request.setExplanation(entry.getExplanation());
        request.setImageId(entry.getImageId());
        request.setMoneyAmount(entry.getMoneyAmount());

        BranchAccountEntryEntity branchEntry = branchAccountEntryService.saveEntry(request, null);
        branchAccountEntryService.approveEntry(branchEntry.getId());
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
