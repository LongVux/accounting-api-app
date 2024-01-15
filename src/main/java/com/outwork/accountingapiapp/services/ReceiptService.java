package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.configs.audit.AuditorAwareImpl;
import com.outwork.accountingapiapp.constants.ReceiptStatusEnum;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.*;
import com.outwork.accountingapiapp.models.payload.requests.GetReceiptTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.ReceiptBill;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRepaymentEntryRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRequest;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptSumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptTableItem;
import com.outwork.accountingapiapp.repositories.ReceiptRepository;
import com.outwork.accountingapiapp.utils.DateTimeUtils;
import com.outwork.accountingapiapp.utils.ReceiptCodeHandler;
import com.outwork.accountingapiapp.utils.Util;
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
    public static final String ERROR_MSG_RECEIPT_OVER_CARD_LIMIT = "Hóa đơn vượt quá hạn mức giao dịch của thẻ";
    public static final String ERROR_MSG_RECEIPT_ALREADY_HAS_CODE = "Hóa đơn đã được tạo mã. Không thể xử lý";
    public static final String ERROR_MSG_RECEIPT_HAS_NO_BILL = "Hóa đơn phải chứa tối thiểu một bill";
    public static final String ERROR_MSG_RECEIPT_NOT_HAVE_CODE = "Hóa đơn chưa được tạo mã. Không thể xử lý";
    public static final String ERROR_MSG_IMAGE_IS_REQUIRED = "Hóa đơn muốn xác nhận thì phải có ảnh chứng từ";

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private BranchService branchService;

    @Autowired
    private CustomerCardService customerCardService;

    @Autowired
    private BillService billService;

    @Autowired
    private Util util;

    public ReceiptEntity getReceipt (@NotNull UUID id) {
        return receiptRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<ReceiptTableItem> getReceiptTableItems (GetReceiptTableItemRequest request) {
        Page<ReceiptEntity> results = receiptRepository.findAll(request, request.retrievePageConfig());
        return results.map(ReceiptTableItem::new);
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

        return sumUpInfo;
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ReceiptEntity saveReceipt (SaveReceiptRequest request, UUID id) {
        ReceiptEntity savedReceipt = ObjectUtils.isEmpty(id) ? new ReceiptEntity() : getReceipt(id);

        savedReceipt.setPercentageFee(request.getPercentageFee());
        savedReceipt.setShipmentFee(request.getShipmentFee());
        savedReceipt.setIntake(request.getIntake());
        savedReceipt.setPayout(request.getPayout());
        savedReceipt.setLoan(request.getLoan());
        savedReceipt.setRepayment(request.getRepayment());
        savedReceipt.setImageId(request.getImageId());;
        savedReceipt.setEmployee(AuditorAwareImpl.getUserFromSecurityContext());
        savedReceipt.setBranch(branchService.getBranchById(request.getBranchId()));
        savedReceipt.setCustomerCard(customerCardService.getCustomerCardById(request.getCustomerCardId()));

        billService.buildBillsForReceipt(request.getReceiptBills(), savedReceipt);

        calculateReceiptTransactionTotal(savedReceipt);
        estimateReceiptProfit(savedReceipt);
        calculateReceiptProfit(savedReceipt);

        validateReceiptForModify(savedReceipt);

        return receiptRepository.save(savedReceipt);
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

        validateReceiptForModify(receipt);

        validateReceiptForApproval(receipt);

        assignReceiptStatus(receipt);
        assignNewReceiptCode(receipt);
        billService.approveBills(receipt.getBills());
        receipt.setCreatedDate(new Date());

        return receiptRepository.save(receipt);
    }

    public ReceiptEntity repayReceiptForEntry (@Valid SaveReceiptRepaymentEntryRequest request) {
        ReceiptEntity receipt = getReceipt(request.getReceiptId());

        if (ObjectUtils.isEmpty(receipt.getCode())) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_NOT_HAVE_CODE);
        }

        receipt.setRepayment(receipt.getRepayment() + request.getRepaidAmount());
        receipt.setCalculatedProfit(receipt.getCalculatedProfit() + request.getRepaidAmount());
        assignReceiptStatus(receipt);

        return receiptRepository.save(receipt);
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void deleteReceipt (@NotNull UUID id) {
        ReceiptEntity receipt = getReceipt(id);

        validateReceiptForModify(receipt);

        billService.deleteBills(receipt.getBills());
        receiptRepository.deleteById(id);
    }

    private void validateReceiptForModify(ReceiptEntity receipt) {
        if (!ObjectUtils.isEmpty(receipt.getCode())) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_ALREADY_HAS_CODE);
        }

        if (CollectionUtils.isEmpty(receipt.getBills())) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_HAS_NO_BILL);
        }

        if (receipt.getIntake() + receipt.getPayout() > receipt.getCustomerCard().getPaymentLimit()) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_OVER_CARD_LIMIT);
        }

        validateReceiptBalance(receipt);
    }

    private void validateReceiptForApproval (ReceiptEntity receipt) {
        if (ObjectUtils.isEmpty(receipt.getImageId())) {
            throw new InvalidDataException(ERROR_MSG_IMAGE_IS_REQUIRED);
        }
    }

    private void validateReceiptBalance (ReceiptEntity receipt) {
        double totalFee = receipt.getBills().stream().mapToDouble(BillEntity::getFee).sum() + receipt.getShipmentFee();

        if (receipt.getTransactionTotal() < totalFee + receipt.getPayout() - receipt.getIntake() - receipt.getLoan()) {
            throw new InvalidDataException(ERROR_MSG_IMBALANCED_RECEIPT);
        }

        if (totalFee > receipt.getIntake() + receipt.getLoan()) {
            throw new InvalidDataException(ERROR_MSG_IMBALANCED_RECEIPT);
        }
    }

    private void calculateReceiptTransactionTotal (ReceiptEntity receipt) {
         receipt.setTransactionTotal(
                 receipt.getBills().stream()
                .map(BillEntity::getMoneyAmount)
                .mapToDouble(Double::doubleValue)
                .sum()
                + receipt.getShipmentFee()
         );
    }

    private void estimateReceiptProfit (ReceiptEntity receipt) {
        double estimatedProfit = receipt.getBills().stream()
                .map(bill -> bill.getFee() - bill.getMoneyAmount()*(1 - bill.getPosFeeStamp() / 100))
                .mapToDouble(Double::doubleValue)
                .sum()
                + receipt.getShipmentFee();

        receipt.setEstimatedProfit(estimatedProfit);
    }

    private void calculateReceiptProfit (ReceiptEntity receipt) {
        double billProfitSum = receipt.getBills().stream()
                .map(BillEntity::getReturnFromBank)
                .mapToDouble(Double::doubleValue)
                .sum();

        receipt.setCalculatedProfit(billProfitSum - receipt.getLoan() + receipt.getRepayment() + receipt.getShipmentFee());
    }

    private void assignNewReceiptCode (ReceiptEntity receipt) {
        Optional<ReceiptEntity> latestReceipt =
                receiptRepository.findFirstByCodeNotNullAndBranchAndEmployeeAndCreatedDateBetweenOrderByCreatedDateDesc(
                        receipt.getBranch(),
                        receipt.getEmployee(),
                        DateTimeUtils.atStartOfDay(new Date()),
                        DateTimeUtils.atEndOfDay(new Date())
                );

        String newCode = ReceiptCodeHandler.generateReceiptCode(
                receipt.getBranch().getCode(),
                receipt.getEmployee().getCode(),
                latestReceipt.map(ReceiptEntity::getCode).orElse(null)
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
