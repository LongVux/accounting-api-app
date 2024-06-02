package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.configs.audit.AuditorAwareImpl;
import com.outwork.accountingapiapp.constants.ReceiptStatusEnum;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.*;
import com.outwork.accountingapiapp.models.payload.requests.GetReceiptTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveNoteRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRepaymentEntryRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRequest;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptSumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptTableItem;
import com.outwork.accountingapiapp.repositories.ReceiptRepository;
import com.outwork.accountingapiapp.utils.DateTimeUtils;
import com.outwork.accountingapiapp.utils.ReceiptCodeHandler;
import com.outwork.accountingapiapp.utils.Util;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
public class ReceiptService {
    public static final String ERROR_MSG_IMBALANCED_RECEIPT = "Hóa đơn cân đối kế toán không hợp lệ";
    public static final String ERROR_MSG_RECEIPT_ALREADY_HAS_CODE = "Hóa đơn đã được tạo mã. Không thể xử lý";
    public static final String ERROR_MSG_RECEIPT_HAS_NO_BILL = "Hóa đơn phải chứa tối thiểu một bill";
    public static final String ERROR_MSG_RECEIPT_NOT_HAVE_CODE = "Hóa đơn chưa được tạo mã. Không thể xử lý";
    public static final String ERROR_MSG_IMAGE_IS_REQUIRED = "Hóa đơn muốn xác nhận thì phải có ảnh chứng từ";
    public static final String ERROR_MSG_EXPIRED_CUSTOMER_CARD = "Thẻ khách đã hết hạn, không thể dùng cho hóa đơn này";
    public static final String ERROR_MSG_INTAKE_EXCEED_PRE_PAID_FEE = "Số tiền phải thu của hóa đơn vượt quá phí đã ứng của thẻ khách";
    public static final String ERROR_MSG_USER_CANNOT_USE_PRE_PAID_FEE = "Người xác nhận chi trả hóa đơn với tiền đã ứng phải là %s";
    public static final String ERROR_MSG_CAN_NOT_DETERMINE_PRE_PAID_FEE_HOLDER = "Hệ thống không xác định được người đang giữ số tiền đã ứng";
    public static final String ERROR_MSG_SOME_POS_NOT_BELONG_TO_THE_RECEIPT_BRANCH = "Một số POS không thuộc về chi nhánh của hóa đơn này";
    public static final String ERROR_MSG_USER_DOES_NOT_HAVE_RIGHT_TO_SAVE_RECEIPT_IN_THIS_BRANCH = "Khách hàng không có quyền lưu hóa đơn trên chi nhánh này";
    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private BranchService branchService;

    @Autowired
    private CustomerCardService customerCardService;

    @Autowired
    private BillService billService;

    @Autowired
    private FileStoringService fileStoringService;

    @Autowired
    private BranchAccountEntryService branchAccountEntryService;

    @Autowired
    private Util util;

    public ReceiptEntity getReceipt (@NotNull UUID id) {
        return receiptRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<ReceiptTableItem> getReceiptTableItems (GetReceiptTableItemRequest request) {
        Page<ReceiptEntity> results = receiptRepository.findAll(request, request.retrievePageConfig());

        return reCalculateReceiptsProfit(results).map(ReceiptTableItem::new);
    }

    public Page<ReceiptEntity> reCalculateReceiptsProfit (Page<ReceiptEntity> receiptEntities) {
        for (ReceiptEntity receipt: receiptEntities) {
            if (!receipt.isSkipRecalculateProfit()) {
                calculateReceiptProfit(receipt);
                receipt.setSkipRecalculateProfit(true);
            }
        }

        receiptRepository.saveAll(receiptEntities);
        return receiptEntities;
    }

    public ReceiptSumUpInfo getReceiptSumUpInfo (GetReceiptTableItemRequest request) {
        List<Double> result = util.getSumsBySpecifications(Collections.singletonList(request), ReceiptEntity.getSumUpFields(), ReceiptEntity.class);

        ReceiptSumUpInfo sumUpInfo = new ReceiptSumUpInfo();

        sumUpInfo.setTotal(Optional.ofNullable(result.get(0)).orElse(0d));
        sumUpInfo.setTotalIntake(Optional.ofNullable(result.get(1)).orElse(0d));
        sumUpInfo.setTotalPayout(Optional.ofNullable(result.get(2)).orElse(0d));
        sumUpInfo.setTotalLoan(Optional.ofNullable(result.get(3)).orElse(0d));
        sumUpInfo.setTotalRepayment(Optional.ofNullable(result.get(4)).orElse(0d));
        sumUpInfo.setTotalEstimatedProfit(Optional.ofNullable(result.get(5)).orElse(0d));
        sumUpInfo.setTotalCalculatedProfit(Optional.ofNullable(result.get(6)).orElse(0d));
        sumUpInfo.setTotalShipmentFee(Optional.ofNullable(result.get(7)).orElse(0d));

        return sumUpInfo;
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ReceiptEntity saveReceipt (SaveReceiptRequest request, UUID id) {
        ReceiptEntity savedReceipt = ObjectUtils.isEmpty(id) ? new ReceiptEntity() : getReceipt(id);

        savedReceipt.setPercentageFee(request.getPercentageFee());
        savedReceipt.setShipmentFee(Optional.of(request.getShipmentFee()).orElse(0d));
        savedReceipt.setIntake(request.getIntake());
        savedReceipt.setPayout(request.getPayout());
        savedReceipt.setLoan(request.getLoan());
        savedReceipt.setRepayment(request.getRepayment());
        savedReceipt.setImageId(request.getImageId());

        // can not update the employee once created
        if (ObjectUtils.isEmpty(id)) {
             savedReceipt.setEmployee(AuditorAwareImpl.getUserFromSecurityContext());
        }

        savedReceipt.setBranch(branchService.getBranchById(request.getBranchId()));
        savedReceipt.setCustomerCard(customerCardService.getCustomerCardById(request.getCustomerCardId()));
        savedReceipt.setUsingCardPrePayFee(request.isUsingCardPrePayFee());
        savedReceipt.setAcceptExceededFee(request.isAcceptExceededFee());

        billService.buildBillsForReceipt(request.getReceiptBills(), savedReceipt);

        calculateReceiptTransactionTotal(savedReceipt);
        estimateReceiptProfit(savedReceipt);
        calculateReceiptProfit(savedReceipt);

        validateReceiptForModify(savedReceipt);

        return receiptRepository.save(savedReceipt);
    }

    public void saveReceiptNote (SaveNoteRequest request) {
        ReceiptEntity receipt = getReceipt(request.getId());
        receipt.setNote(request.getNote());

        receiptRepository.save(receipt);
    }

    public void remarkReCalculateReceiptsProfitFromBillsMatch (List<BillEntity> bills) {
        Map<UUID, ReceiptEntity> receiptMap = new HashMap<>();

        for (BillEntity bill: bills) {
            receiptMap.put(bill.getReceipt().getId(), bill.getReceipt());
        }

        for (ReceiptEntity receipt : receiptMap.values()) {
            receipt.setSkipRecalculateProfit(false);
        }

        receiptRepository.saveAll(receiptMap.values());
    }

    public ReceiptEntity reCalculatedReceipt (UUID id) {
        ReceiptEntity receipt = getReceipt(id);

        calculateReceiptTransactionTotal(receipt);
        estimateReceiptProfit(receipt);
        calculateReceiptProfit(receipt);

        return receiptRepository.save(receipt);
    }

    public ReceiptEntity approveReceiptForEntry (@NotNull UUID id) {
        ReceiptEntity receipt = getReceipt(id);
        UserEntity approver = AuditorAwareImpl.getUserFromSecurityContext();
        
        validateReceiptForModify(receipt);

        validateReceiptForApproval(receipt);

        receipt.setConfirmedDate(new Date());
        receipt.setApproverCode(approver.getCode());
        assignReceiptStatus(receipt);
        assignNewReceiptCode(receipt);
        billService.approveBills(receipt.getBills());

        return receiptRepository.save(receipt);
    }

    public ReceiptEntity repayReceiptForEntry (@Valid SaveReceiptRepaymentEntryRequest request) {
        ReceiptEntity receipt = getReceipt(request.getReceiptId());

        if (ObjectUtils.isEmpty(receipt.getCode())) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_NOT_HAVE_CODE);
        }

        receipt.setRepayment(receipt.getRepayment() + request.getRepaidAmount());
        //receipt.setCalculatedProfit(receipt.getCalculatedProfit() + request.getRepaidAmount());
        assignReceiptStatus(receipt);

        return receiptRepository.save(receipt);
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void deleteReceipt (@NotNull UUID id, String explanation) {
        ReceiptEntity receipt = getReceipt(id);

        if (!ObjectUtils.isEmpty(receipt.getCode())) {
            branchAccountEntryService.handleDeleteConfirmedReceiptRelatedEntries(receipt);
            branchAccountEntryService.handleRefundForDeletedConfirmedReceipt(receipt, explanation);
        }

        fileStoringService.deleteFile(receipt.getImageId());
        billService.deleteBills(receipt.getBills());
        receiptRepository.deleteById(id);
    }

    private void validateReceiptForModify(ReceiptEntity receipt) {
        UserEntity editor = AuditorAwareImpl.getUserFromSecurityContext();

        if (!ObjectUtils.nullSafeEquals(receipt.getCreatedBy(), editor.getCode()) &&
                editor.getBranchManagementScopes().stream().noneMatch(scope -> ObjectUtils.nullSafeEquals(scope.getBranch().getId(), receipt.getBranch().getId()))) {
            throw new InvalidDataException(ERROR_MSG_USER_DOES_NOT_HAVE_RIGHT_TO_SAVE_RECEIPT_IN_THIS_BRANCH);
        }

        if (!ObjectUtils.isEmpty(receipt.getCode())) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_ALREADY_HAS_CODE);
        }

        if (CollectionUtils.isEmpty(receipt.getBills())) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_HAS_NO_BILL);
        }

        if (receipt.getCustomerCard().getExpiredDate().before(new Date())) {
            throw new InvalidDataException(ERROR_MSG_EXPIRED_CUSTOMER_CARD);
        }

        if (receipt.getBills().stream().anyMatch(
                bill -> bill.getPos().getBranches().stream().noneMatch(
                        branch -> ObjectUtils.nullSafeEquals(branch.getId(), receipt.getBranch().getId())))) {
            throw new InvalidDataException(ERROR_MSG_SOME_POS_NOT_BELONG_TO_THE_RECEIPT_BRANCH);
        }

        if (receipt.isUsingCardPrePayFee() && !receipt.isAcceptExceededFee() && receipt.getIntake() > receipt.getCustomerCard().getPrePaidFee()) {
            throw new InvalidDataException(ERROR_MSG_INTAKE_EXCEED_PRE_PAID_FEE);
        }

        validateReceiptBalance(receipt);
    }

    private void validateReceiptForApproval (ReceiptEntity receipt) {
        if (ObjectUtils.isEmpty(receipt.getImageId())) {
            throw new InvalidDataException(ERROR_MSG_IMAGE_IS_REQUIRED);
        }

        UserEntity approver = AuditorAwareImpl.getUserFromSecurityContext();

        if (receipt.isUsingCardPrePayFee() && !Objects.equals(receipt.getCustomerCard().getPrePaidFeeReceiverCode(), approver.getCode())) {
            if (ObjectUtils.isEmpty(receipt.getCustomerCard().getPrePaidFeeReceiverCode())) {
                throw new InvalidDataException(ERROR_MSG_CAN_NOT_DETERMINE_PRE_PAID_FEE_HOLDER);
            } else {
                throw new InvalidDataException(String.format(ERROR_MSG_USER_CANNOT_USE_PRE_PAID_FEE, receipt.getCustomerCard().getPrePaidFeeReceiverCode()));
            }
        }

        if (receipt.isUsingCardPrePayFee() && !receipt.isAcceptExceededFee() && receipt.getIntake() > receipt.getCustomerCard().getPrePaidFee()) {
            throw new InvalidDataException(ERROR_MSG_INTAKE_EXCEED_PRE_PAID_FEE);
        }
    }

    private void validateReceiptBalance (ReceiptEntity receipt) {
        double totalBillFee = receipt.getBills().stream().mapToDouble(BillEntity::getFee).sum();

        if (receipt.getTransactionTotal() - receipt.getShipmentFee() < totalBillFee + receipt.getPayout() - receipt.getIntake() - receipt.getLoan()) {
            throw new InvalidDataException(ERROR_MSG_IMBALANCED_RECEIPT);
        }
    }

    private void calculateReceiptTransactionTotal (ReceiptEntity receipt) {
         receipt.setTransactionTotal(
                 receipt.getBills().stream()
                .map(BillEntity::getMoneyAmount)
                .mapToDouble(Double::doubleValue)
                .sum()
         );
    }

    private void estimateReceiptProfit (ReceiptEntity receipt) {
        double estimatedProfit = receipt.getBills().stream()
                .map(bill -> bill.getFee() - (bill.getMoneyAmount() - bill.getEstimatedReturnFromBank()))
                .mapToDouble(Double::doubleValue)
                .sum()
                + receipt.getShipmentFee();

        receipt.setEstimatedProfit(estimatedProfit);
    }

    private void calculateReceiptProfit (ReceiptEntity receipt) {
        double billReturnSum = receipt.getBills().stream()
                .map(bill -> bill.getFee() + bill.getReturnFromBank() - bill.getMoneyAmount())
                .mapToDouble(Double::doubleValue)
                .sum();
        receipt.setCalculatedProfit(billReturnSum + receipt.getShipmentFee());
    }

    private void assignNewReceiptCode (ReceiptEntity receipt) {
        Optional<ReceiptEntity> latestReceipt =
                receiptRepository.findFirstByCodeNotNullAndBranchAndEmployeeAndCreatedDateBetweenOrderByConfirmedDateDesc(
                        receipt.getBranch(),
                        receipt.getEmployee(),
                        DateTimeUtils.atStartOfDay(receipt.getCreatedDate()),
                        DateTimeUtils.atEndOfDay(receipt.getCreatedDate())
                );

        String newCode = ReceiptCodeHandler.generateReceiptCode(
                receipt.getBranch().getCode(),
                receipt.getEmployee().getCode(),
                latestReceipt.map(ReceiptEntity::getCode).orElse(null),
                receipt.getCreatedDate()
        );

        receipt.setCode(newCode);
    }

    private void assignReceiptStatus (ReceiptEntity receipt) {
        if (receipt.getCalculatedProfit() < 0) {
            receipt.setReceiptStatus(ReceiptStatusEnum.LOANED);
        } else {
            receipt.setReceiptStatus(ReceiptStatusEnum.COMPLETED);
        }
    }
}
