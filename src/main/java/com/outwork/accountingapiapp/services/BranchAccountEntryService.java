package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.configs.audit.AuditorAwareImpl;
import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.*;
import com.outwork.accountingapiapp.models.payload.requests.*;
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
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class BranchAccountEntryService {
    public static final String ERROR_MSG_INVALID_ACTION_ON_ENTRY = "Không thể thực hiện hành động này trên bút toán " +
            "được chọn";

    public static final String ENTRY_TYPE_USING_PRE_PAID_FEE_FOR_RECEIPT = "HPU-%s";
    public static final String ENTRY_TYPE_RETURN_PRE_PAID_FEE = "Hoàn phí đã ứng";
    public static final String ENTRY_TYPE_COLLECT_PRE_PAID_FEE = "Thu phí muốn ứng";
    public static final String EXPLANATION_ADJUST_PREPAID_FEE_FOR_CARD = "Thay đổi phí ứng trước của thẻ %s - %s - %s";

    public static final String ENTRY_TYPE_DELETE_CONFIRMED_RECEIPT = "Hủy hóa đơn";

    public static final String EXPLANATION_DELETE_CONFIRMED_RECEIPT = "Mã hóa đơn bị hủy: %s\nNgười xác nhận: %s\nNgười tạo: %s\n";
    @Autowired
    private BranchAccountEntryRepository branchAccountEntryRepository;

    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private BranchService branchService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerCardService customerCardService;

    @Autowired
    private Util util;

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ReceiptEntity confirmReceiptEntry(@Valid SaveReceiptEntryRequest request) {
        ReceiptEntity receipt = receiptService.approveReceiptForEntry(request.getReceiptId());

        UserEntity approverAfterBalance = handleAdjustBalanceForApprover(receipt);

        handleCreateEntriesForReceipt(receipt, request, approverAfterBalance);

        if (receipt.isUsingCardPrePayFee()) {
            handleAdjustBalanceForCard(receipt);
        }

        return receipt;
    }

    private void handleCreateEntriesForReceipt (ReceiptEntity receipt, SaveReceiptEntryRequest request, UserEntity approverAfterBalance) {
        List<BranchAccountEntryEntity> entities = generateBranchAccountEntriesFromReceipt(receipt, request, approverAfterBalance);
        branchAccountEntryRepository.saveAll(entities);
    }

    private UserEntity handleAdjustBalanceForApprover (ReceiptEntity receipt) {
        UserEntity approver = AuditorAwareImpl.getUserFromSecurityContext();

        Double adjustedBalanceAmount = receipt.getIntake() - receipt.getPayout() + receipt.getRepayment();

        if (receipt.isUsingCardPrePayFee()) {
            // when using the pre-paid fee, the approver do not actually receive any intake
            adjustedBalanceAmount -= receipt.getIntake();

            if (receipt.isAcceptExceededFee()) {
                adjustedBalanceAmount = receipt.getIntake() - receipt.getCustomerCard().getPrePaidFee();
            }
        }

        approver.setAccountBalance(approver.getAccountBalance() + adjustedBalanceAmount);

        return userService.saveUserEntity(approver);
    }

    private void handleAdjustBalanceForCard (ReceiptEntity receipt) {
        double adjustedBalanceAmount =  receipt.getIntake();

        if (adjustedBalanceAmount > receipt.getCustomerCard().getPrePaidFee() && receipt.isAcceptExceededFee()) {
            adjustedBalanceAmount = receipt.getCustomerCard().getPrePaidFee();
        }

        receipt.getCustomerCard().setPrePaidFee(receipt.getCustomerCard().getPrePaidFee() - adjustedBalanceAmount);

        customerCardService.saveCustomerCardEntity(receipt.getCustomerCard());
    }

    public void handleDeleteConfirmedReceiptRelatedEntries (ReceiptEntity receipt) {
        UserEntity approver = userService.getUserEntityByCode(receipt.getApproverCode());

        List<BranchAccountEntryEntity> relatedEntries = branchAccountEntryRepository.findByReceipt_Id(receipt.getId());

        relatedEntries.forEach(entry -> entry.setReceipt(null));

        branchAccountEntryRepository.saveAll(relatedEntries);
    }

    public void handleRefundForDeletedConfirmedReceipt (ReceiptEntity receipt, String explanation) {
        BranchAccountEntryEntity deleteReceiptEntry = new BranchAccountEntryEntity();
        double refundAmount = receipt.getPayout() - receipt.getIntake() + receipt.getLoan() - receipt.getRepayment();

        UserEntity approver = userService.getUserEntityByCode(receipt.getApproverCode());
        approver.setAccountBalance(approver.getAccountBalance() + refundAmount);

        String defaultExplanation = String.format(EXPLANATION_DELETE_CONFIRMED_RECEIPT, receipt.getCode(), receipt.getApproverCode(), receipt.getEmployee().getCode());

        deleteReceiptEntry.setEntryType(ENTRY_TYPE_DELETE_CONFIRMED_RECEIPT);
        deleteReceiptEntry.setBranch(receipt.getBranch());
        deleteReceiptEntry.setExplanation(defaultExplanation + explanation);
        deleteReceiptEntry.setMoneyAmount(Math.abs(refundAmount));
        deleteReceiptEntry.setEntryStatus(AccountEntryStatusEnum.APPROVED);
        deleteReceiptEntry.setRemainingBalance(approver.getAccountBalance());

        if (refundAmount < 0) {
            deleteReceiptEntry.setTransactionType(TransactionTypeEnum.PAYOUT);
        }

        if (refundAmount > 0) {
            deleteReceiptEntry.setTransactionType(TransactionTypeEnum.INTAKE);
        }

        deleteReceiptEntry.setEntryCode(getNewBranchEntryCode(deleteReceiptEntry));

        userService.saveUserEntity(approver);
        branchAccountEntryRepository.save(deleteReceiptEntry);
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ReceiptEntity confirmRepayReceipt(@Valid SaveReceiptRepaymentEntryRequest request) {
        ReceiptEntity receipt = receiptService.repayReceiptForEntry(request);

        BranchAccountEntryEntity repaidEntry = BranchAccountEntryEntity.createSystemBranchAccountEntry(
                receipt,
                request.getExplanation(),
                TransactionTypeEnum.REPAYMENT,
                request.getRepaidAmount(),
                request.getImageId()
        );

        repaidEntry.setEntryCode(getNewBranchEntryCode(repaidEntry));

        UserEntity approver = AuditorAwareImpl.getUserFromSecurityContext();

        approver.setAccountBalance(approver.getAccountBalance() + repaidEntry.getMoneyAmount());

        userService.saveUserEntity(approver);

        repaidEntry.setRemainingBalance(approver.getAccountBalance());
        branchAccountEntryRepository.save(repaidEntry);

        return receipt;
    }

    public BranchAccountEntryEntity createCardAdjustPrePaidFeeEntry (CustomerCardEntity customerCard, AdjustPrePaidFeeRequest request) {
        UserEntity editor = AuditorAwareImpl.getUserFromSecurityContext();
        double adjustment = request.getPrePaidFee() - Optional.of(customerCard.getPrePaidFee()).orElse(0d);

        if (adjustment == 0) {
            return null;
        }

        BranchAccountEntryEntity entry = new BranchAccountEntryEntity();
        entry.setEntryStatus(AccountEntryStatusEnum.APPROVED);
        entry.setMoneyAmount(Math.abs(adjustment));
        entry.setExplanation(String.format(EXPLANATION_ADJUST_PREPAID_FEE_FOR_CARD, customerCard.getName(), customerCard.getBank(), customerCard.getAccountNumber()));
        entry.setImageId(request.getImageId());
        entry.setBranch(branchService.getBranchById(request.getBranchId()));


        if (adjustment < 0) {
            entry.setEntryType(ENTRY_TYPE_RETURN_PRE_PAID_FEE);
            entry.setTransactionType(TransactionTypeEnum.PAYOUT);
        }

        if (adjustment > 0) {
            entry.setEntryType(ENTRY_TYPE_COLLECT_PRE_PAID_FEE);
            entry.setTransactionType(TransactionTypeEnum.INTAKE);
        }

        entry.setEntryCode(getNewBranchEntryCode(entry));

        editor.setAccountBalance(editor.getAccountBalance() + adjustment);

        entry.setRemainingBalance(editor.getAccountBalance());

        userService.saveUserEntity(editor);

        return branchAccountEntryRepository.save(entry);
    }

    public BranchAccountEntryEntity getEntryById(@NotNull UUID id) {
        return branchAccountEntryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<BranchAccountEntryTableItem> getBranchAccountEntryTableItems(GetBranchAccountEntryTableItemRequest request) {
        return branchAccountEntryRepository.findAll(request, request.retrievePageConfig()).map(BranchAccountEntryTableItem::new);
    }

    public AccountEntrySumUpInfo getBranchAccountEntrySumUpInfo(GetBranchAccountEntryTableItemRequest request) {
        Map<Object, Double> queryResult = util.getGroupedSumsBySpecification(request,
                BranchAccountEntryEntity.FIELD_TRANSACTION_TYPE, BranchAccountEntryEntity.FIELD_MONEY_AMOUNT,
                BranchAccountEntryEntity.class);

        AccountEntrySumUpInfo response = new AccountEntrySumUpInfo();

        response.setTotalIntake(Optional.ofNullable(queryResult.get(TransactionTypeEnum.INTAKE)).orElse(0d));
        response.setTotalPayout(Optional.ofNullable(queryResult.get(TransactionTypeEnum.PAYOUT)).orElse(0d));
        response.setTotalLoan(Optional.ofNullable(queryResult.get(TransactionTypeEnum.LOAN)).orElse(0d));
        response.setTotalRepayment(Optional.ofNullable(queryResult.get(TransactionTypeEnum.REPAYMENT)).orElse(0d));
        response.setTotal(response.getTotalIntake() - response.getTotalPayout() - response.getTotalLoan() + response.getTotalRepayment());

        return response;
    }

    public BranchAccountEntryEntity saveEntry(@Valid SaveBranchAccountEntryRequest request, UUID id) {
        BranchAccountEntryEntity savedEntry = ObjectUtils.isEmpty(id) ? new BranchAccountEntryEntity() :
                getEntryById(id);

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

    public void saveBranchAccountEntryNote (SaveNoteRequest request) {
        BranchAccountEntryEntity branchAccountEntry = getEntryById(request.getId());
        branchAccountEntry.setNote(request.getNote());

        branchAccountEntryRepository.save(branchAccountEntry);
    }

    public BranchAccountEntryEntity approveEntry(@NotNull UUID id) {
        BranchAccountEntryEntity approvedEntry = getEntryById(id);

        validateAccountEntryForModification(approvedEntry);

        approvedEntry.setEntryCode(getNewBranchEntryCode(approvedEntry));
        approvedEntry.setEntryStatus(AccountEntryStatusEnum.APPROVED);
        approvedEntry.setCreatedDate(new Date());

        UserEntity approver = AuditorAwareImpl.getUserFromSecurityContext();

        if (List.of(TransactionTypeEnum.INTAKE, TransactionTypeEnum.REPAYMENT).contains(approvedEntry.getTransactionType())) {
            approver.setAccountBalance(approver.getAccountBalance() + approvedEntry.getMoneyAmount());
        } else {
            approver.setAccountBalance(approver.getAccountBalance() - approvedEntry.getMoneyAmount());
        }

        userService.saveUserEntity(approver);
        approvedEntry.setRemainingBalance(approver.getAccountBalance());

        return branchAccountEntryRepository.save(approvedEntry);
    }

    public void deleteBranchAccountEntry(@NotNull UUID id) {
        BranchAccountEntryEntity entry = getEntryById(id);

        validateAccountEntryForModification(entry);

        branchAccountEntryRepository.deleteById(id);
    }

    private void validateAccountEntryForModification(BranchAccountEntryEntity entry) {
        if (!ObjectUtils.isEmpty(entry.getEntryCode())) {
            throw new InvalidDataException(ERROR_MSG_INVALID_ACTION_ON_ENTRY);
        }
    }

    private List<BranchAccountEntryEntity> generateBranchAccountEntriesFromReceipt(ReceiptEntity receipt,
                                                                                   SaveReceiptEntryRequest request, UserEntity approverAfterBalance) {
        Map<TransactionTypeEnum, Double> receiptEntryMap = new HashMap<>();

        receiptEntryMap.put(TransactionTypeEnum.INTAKE, receipt.getIntake());
        receiptEntryMap.put(TransactionTypeEnum.PAYOUT, receipt.getPayout());
        receiptEntryMap.put(TransactionTypeEnum.LOAN, receipt.getLoan());
        receiptEntryMap.put(TransactionTypeEnum.REPAYMENT, receipt.getRepayment());

        List<BranchAccountEntryEntity> entries = new ArrayList<>(receiptEntryMap.keySet().stream()
            .filter(key -> Optional.ofNullable(receiptEntryMap.get(key)).orElse(0d) != 0d)
            .map(key -> BranchAccountEntryEntity.createSystemBranchAccountEntry(
                    receipt,
                    request.getExplanation(),
                    key,
                    receiptEntryMap.get(key),
                    request.getImageId()
            )).peek(entry ->
                {
                    entry.setEntryCode(getNewBranchEntryCode(entry));
                    if (TransactionTypeEnum.PAYOUT.equals(entry.getTransactionType())) {
                        entry.setRemainingBalance(approverAfterBalance.getAccountBalance());
                    }
                }
            ).toList());

        if (receipt.isUsingCardPrePayFee() && receipt.getIntake() > 0) {
            BranchAccountEntryEntity deductPrePaidFeeEntry = BranchAccountEntryEntity.createSystemBranchAccountEntry(
                    receipt,
                    request.getExplanation(),
                    TransactionTypeEnum.PAYOUT,
                    0,
                    request.getImageId()
            );

            deductPrePaidFeeEntry.setEntryType(String.format(ENTRY_TYPE_USING_PRE_PAID_FEE_FOR_RECEIPT, receipt.getCode()));

            entries.forEach(entry -> {
                if (entry.getTransactionType() == TransactionTypeEnum.PAYOUT) {
                    long currentTimeStamp = (new Date()).getTime();
                    entry.setTimeStampSeq(currentTimeStamp);
                    deductPrePaidFeeEntry.setEntryCode(getNewBranchEntryCode(entry));
                    deductPrePaidFeeEntry.setTimeStampSeq(currentTimeStamp + 1);
                }
            });

            if (receipt.isAcceptExceededFee()) {
                deductPrePaidFeeEntry.setMoneyAmount(receipt.getCustomerCard().getPrePaidFee());
            } else {
                deductPrePaidFeeEntry.setMoneyAmount(receipt.getIntake());
            }

            entries.add(deductPrePaidFeeEntry);
        }

        return entries;
    }

    private String getNewBranchEntryCode(BranchAccountEntryEntity entry) {
        Optional<BranchAccountEntryEntity> latestEntry =
                ObjectUtils.isEmpty(entry.getEntryCode()) ?
                branchAccountEntryRepository.findFirstByEntryCodeNotNullAndBranchAndTransactionTypeAndCreatedDateBetweenOrderByCreatedDateDescTimeStampSeqDesc(
                        entry.getBranch(),
                        entry.getTransactionType(),
                        DateTimeUtils.atStartOfDay(new Date()),
                        DateTimeUtils.atEndOfDay(new Date())
                ): Optional.of(entry);

        return BranchAccountEntryCodeHandler.generateAccountEntryCode(
                entry.getBranch().getCode(),
                entry.getTransactionType(),
                latestEntry.map(BranchAccountEntryEntity::getEntryCode).orElse(null)
        );
    }
}
