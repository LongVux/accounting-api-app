package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetBranchAccountEntryTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveBranchAccountEntryRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptEntryRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRepaymentEntryRequest;
import com.outwork.accountingapiapp.models.payload.responses.AccountEntrySumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.BranchAccountEntryTableItem;
import com.outwork.accountingapiapp.repositories.BillRepository;
import com.outwork.accountingapiapp.repositories.BranchAccountEntryRepository;
import com.outwork.accountingapiapp.utils.BranchAccountEntryCodeHandler;
import com.outwork.accountingapiapp.utils.DateTimeUtils;
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
public class BranchAccountEntryService {
    public static final String ERROR_MSG_INVALID_ACTION_ON_ENTRY = "Không thể thực hiện hành động này trên bút toán được chọn";

    @Autowired
    private BranchAccountEntryRepository branchAccountEntryRepository;

    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private BranchService branchService;

    @Autowired
    private Util util;

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ReceiptEntity confirmReceiptEntry (@Valid SaveReceiptEntryRequest request) {
        ReceiptEntity receipt = receiptService.approveReceiptForEntry(request.getReceiptId());

        List<BranchAccountEntryEntity> entities = generateBranchAccountEntriesFromReceipt(receipt, request.getExplanation());

        branchAccountEntryRepository.saveAll(entities);

        return receipt;
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ReceiptEntity confirmRepayReceipt (@Valid SaveReceiptRepaymentEntryRequest request) {
        ReceiptEntity receipt = receiptService.repayReceiptForEntry(request);

        BranchAccountEntryEntity repaidEntry = new BranchAccountEntryEntity(
                receipt,
                request.getExplanation(),
                TransactionTypeEnum.REPAYMENT,
                request.getRepaidAmount()
        );

        repaidEntry.setEntryCode(getNewBranchEntryCode(repaidEntry));
        repaidEntry.setEntryStatus(AccountEntryStatusEnum.APPROVED);

        branchAccountEntryRepository.save(repaidEntry);

        return receipt;
    }

    public BranchAccountEntryEntity getEntryById (@NotNull UUID id) {
        return branchAccountEntryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<BranchAccountEntryTableItem> getBranchAccountEntryTableItems (GetBranchAccountEntryTableItemRequest request) {
        return branchAccountEntryRepository.findAll(request, request.retrievePageConfig()).map(BranchAccountEntryTableItem::new);
    }

    public AccountEntrySumUpInfo getBranchAccountEntrySumUpInfo (GetBranchAccountEntryTableItemRequest request) {
        Map<Object, Double> queryResult = util.getGroupedSumsBySpecification(request, BranchAccountEntryEntity.FIELD_TRANSACTION_TYPE, BranchAccountEntryEntity.FIELD_MONEY_AMOUNT, BranchAccountEntryEntity.class);

        AccountEntrySumUpInfo response = new AccountEntrySumUpInfo();

        response.setTotalIntake(Optional.ofNullable(queryResult.get(TransactionTypeEnum.INTAKE)).orElse(0d));
        response.setTotalPayout(Optional.ofNullable(queryResult.get(TransactionTypeEnum.PAYOUT)).orElse(0d));
        response.setTotalLoan(Optional.ofNullable(queryResult.get(TransactionTypeEnum.LOAN)).orElse(0d));
        response.setTotalRepayment(Optional.ofNullable(queryResult.get(TransactionTypeEnum.REPAYMENT)).orElse(0d));
        response.setTotal(response.getTotalIntake() - response.getTotalPayout() - response.getTotalLoan() + response.getTotalRepayment());

        return response;
    }

    public BranchAccountEntryEntity saveEntry (@Valid SaveBranchAccountEntryRequest request, UUID id) {
        BranchAccountEntryEntity savedEntry = ObjectUtils.isEmpty(id) ? new BranchAccountEntryEntity() : getEntryById(id);

        savedEntry.setEntryType(request.getEntryType());
        savedEntry.setMoneyAmount(request.getMoneyAmount());
        savedEntry.setTransactionType(request.getTransactionType());
        savedEntry.setExplanation(request.getExplanation());
        savedEntry.setBranch(branchService.getBranchById(request.getBranchId()));
        savedEntry.setImageId(request.getImageId());
        savedEntry.setEntryStatus(AccountEntryStatusEnum.PENDING);

        validateAccountEntryForModification(savedEntry);

        return branchAccountEntryRepository.save(savedEntry);
    }

    public BranchAccountEntryEntity approveEntry (@NotNull UUID id) {
        BranchAccountEntryEntity approvedEntry = getEntryById(id);

        validateAccountEntryForModification(approvedEntry);

        approvedEntry.setEntryCode(getNewBranchEntryCode(approvedEntry));
        approvedEntry.setEntryStatus(AccountEntryStatusEnum.APPROVED);
        approvedEntry.setCreatedDate(new Date());

        return branchAccountEntryRepository.save(approvedEntry);
    }

    public void deleteBranchAccountEntry (@NotNull UUID id) {
        BranchAccountEntryEntity entry = getEntryById(id);

        validateAccountEntryForModification(entry);

        branchAccountEntryRepository.deleteById(id);
    }

    private void validateAccountEntryForModification(BranchAccountEntryEntity entry) {
        if (!ObjectUtils.isEmpty(entry.getEntryCode())) {
            throw new InvalidDataException(ERROR_MSG_INVALID_ACTION_ON_ENTRY);
        }
    }

    private List<BranchAccountEntryEntity> generateBranchAccountEntriesFromReceipt (ReceiptEntity receipt, String explanation) {
        Map<TransactionTypeEnum, Double> receiptEntryMap = new HashMap<>();

        receiptEntryMap.put(TransactionTypeEnum.INTAKE, receipt.getIntake());
        receiptEntryMap.put(TransactionTypeEnum.PAYOUT, receipt.getPayout());
        receiptEntryMap.put(TransactionTypeEnum.LOAN, receipt.getLoan());
        receiptEntryMap.put(TransactionTypeEnum.REPAYMENT, receipt.getRepayment());

        return receiptEntryMap.keySet().stream()
                .filter(key -> Optional.ofNullable(receiptEntryMap.get(key)).orElse(0d) != 0d)
                .map(key -> new BranchAccountEntryEntity(
                        receipt,
                        explanation,
                        key,
                        receiptEntryMap.get(key)
                ))
                .peek(entry -> {
                    entry.setEntryCode(getNewBranchEntryCode(entry));
                    entry.setEntryStatus(AccountEntryStatusEnum.APPROVED);
                })
                .toList();
    }

    private String getNewBranchEntryCode (BranchAccountEntryEntity entry) {
        Optional<BranchAccountEntryEntity> latestEntry = branchAccountEntryRepository.findFirstByEntryCodeNotNullAndBranchAndTransactionTypeAndLastModifiedDateBetweenOrderByLastModifiedDateDesc(
                entry.getBranch(),
                entry.getTransactionType(),
                DateTimeUtils.atStartOfDay(new Date()),
                DateTimeUtils.atEndOfDay(new Date())
        );

        return BranchAccountEntryCodeHandler.generateAccountEntryCode(
                entry.getBranch().getCode(),
                entry.getTransactionType(),
                latestEntry.map(BranchAccountEntryEntity::getEntryCode).orElse(null)
        );
    }
}
