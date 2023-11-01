package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.configs.audit.AuditorAwareImpl;
import com.outwork.accountingapiapp.constants.ReceiptStatusEnum;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.*;
import com.outwork.accountingapiapp.models.payload.requests.GetReceiptTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.ReceiptBill;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRepaymentEntryRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRequest;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptTableItem;
import com.outwork.accountingapiapp.repositories.ReceiptRepository;
import com.outwork.accountingapiapp.utils.DateTimeUtils;
import com.outwork.accountingapiapp.utils.ReceiptCodeHandler;
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
    public static final String ERROR_MSG_RECEIPT_INVALID_TO_APPROVE = "Hóa đơn không hợp lệ để tạo bút toán";

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private BranchService branchService;

    @Autowired
    private CustomerCardService customerCardService;

    @Autowired
    private BillService billService;

    public ReceiptEntity getReceipt (@NotNull UUID id) {
        return receiptRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<ReceiptTableItem> getReceiptTableItems (GetReceiptTableItemRequest request) {
        Page<ReceiptEntity> results = receiptRepository.findAll(request, request.retrievePageConfig());
        return results.map(ReceiptTableItem::new);
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ReceiptEntity saveReceipt (@Valid SaveReceiptRequest request, UUID id) {
        validateSaveReceiptRequestBalance(request);

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

    public ReceiptEntity approveReceiptForEntry (@NotNull UUID id) {
        ReceiptEntity receipt = getReceipt(id);

        validateReceiptForEntry(receipt);

        assignReceiptStatus(receipt);
        assignNewReceiptCode(receipt);
        billService.assignNewBillCodes(receipt.getBills());

        return receiptRepository.save(receipt);
    }

    public ReceiptEntity repayReceiptForEntry (@Valid SaveReceiptRepaymentEntryRequest request) {
        ReceiptEntity receipt = getReceipt(request.getReceiptId());

        validateReceiptForEntry(receipt);

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

    private void validateReceiptForEntry (ReceiptEntity receipt) {
        if (!ObjectUtils.isEmpty(receipt.getCode())) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_INVALID_TO_APPROVE);
        }

        validateReceiptBalance(receipt);
    }

    private void validateReceiptForModify(ReceiptEntity receipt) {
        if (!ObjectUtils.isEmpty(receipt.getCode())) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_ALREADY_HAS_CODE);
        }

        if (CollectionUtils.isEmpty(receipt.getBills())) {
            throw new InvalidDataException(ERROR_MSG_RECEIPT_HAS_NO_BILL);
        }
    }

    private void validateSaveReceiptRequestBalance (SaveReceiptRequest request) {
        double givenReceiptBalance = - request.getIntake() + request.getPayout() + request.getLoan() - request.getRepayment();
        double transactionTotal = request.getReceiptBills().stream()
                .map(ReceiptBill::getMoneyAmount)
                .mapToInt(Integer::intValue)
                .sum()
                + request.getShipmentFee();

        if (Math.abs(givenReceiptBalance) > Math.abs(transactionTotal)) {
            throw new InvalidDataException(ERROR_MSG_IMBALANCED_RECEIPT);
        }
    }

    private void validateReceiptBalance (ReceiptEntity receipt) {
        double givenReceiptBalance = - receipt.getIntake() + receipt.getPayout() + receipt.getLoan() - receipt.getRepayment();
        double transactionTotal = receipt.getTransactionTotal();

        if (Math.abs(givenReceiptBalance) > Math.abs(transactionTotal)) {
            throw new InvalidDataException(ERROR_MSG_IMBALANCED_RECEIPT);
        }
    }

    private void calculateReceiptTransactionTotal (ReceiptEntity receipt) {
         receipt.setTransactionTotal(
                 receipt.getBills().stream()
                .map(BillEntity::getMoneyAmount)
                .mapToInt(Integer::intValue)
                .sum()
                + receipt.getShipmentFee()
         );
    }

    private void estimateReceiptProfit (ReceiptEntity receipt) {
        receipt.getBills().forEach(bill -> billService.estimateBillProfit(bill));

        double estimatedProfit = receipt.getBills().stream()
                .map(BillEntity::getEstimatedProfit)
                .mapToDouble(Double::doubleValue)
                .sum()
                + receipt.getShipmentFee();

        receipt.setEstimatedProfit(estimatedProfit);
    }

    private void calculateReceiptProfit (ReceiptEntity receipt) {
        double billProfitSum = receipt.getBills().stream()
                .map(BillEntity::getReturnedProfit)
                .mapToDouble(Double::doubleValue)
                .sum();

        receipt.setCalculatedProfit(billProfitSum + calculateReceiptBalance(receipt));
    }

    private int calculateReceiptBalance (ReceiptEntity receipt) {
        return - receipt.getIntake() + receipt.getPayout() + receipt.getLoan() - receipt.getRepayment();
    }

    private void assignNewReceiptCode (ReceiptEntity receipt) {
        Optional<ReceiptEntity> latestReceipt =
                receiptRepository.findFirstByCodeNotNullAndBranchAndCreatedDateBetweenOrderByCreatedDateDesc(
                        receipt.getBranch(),
                        DateTimeUtils.atStartOfDay(new Date()),
                        DateTimeUtils.atEndOfDay(new Date())
                );

        String newCode = ReceiptCodeHandler.generateReceiptCode(
                receipt.getBranch().getCode(),
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
