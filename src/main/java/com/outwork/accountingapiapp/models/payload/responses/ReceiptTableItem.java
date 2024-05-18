package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.constants.ReceiptStatusEnum;
import com.outwork.accountingapiapp.models.entity.BillEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ReceiptTableItem {
    private UUID id;
    private String code;
    private Date createdDate;
    private ReceiptStatusEnum receiptStatusEnum;
    private double percentageFee;
    private double shipmentFee;
    private double intake;
    private double payout;
    private double loan;
    private double repayment;
    private double totalFee;
    private double transactionTotal;
    private double calculatedProfit;
    private double estimatedProfit;
    private UUID customerCardId;
    private String customerCardName;
    private String customerCardNumber;
    private UUID employeeId;
    private String employeeName;
    private UUID branchId;
    private String branchName;
    private String note;
    private String approverCode;

    public ReceiptTableItem(ReceiptEntity receipt) {
        this.id = receipt.getId();
        this.code = receipt.getCode();
        this.createdDate = receipt.getCreatedDate();
        this.receiptStatusEnum = receipt.getReceiptStatus();
        this.percentageFee = receipt.getPercentageFee();
        this.shipmentFee = receipt.getShipmentFee();
        this.intake = receipt.getIntake();
        this.payout = receipt.getPayout();
        this.loan = receipt.getLoan();
        this.repayment = receipt.getRepayment();
        this.transactionTotal = receipt.getTransactionTotal();
        this.calculatedProfit = receipt.getCalculatedProfit();
        this.estimatedProfit = receipt.getEstimatedProfit();
        this.customerCardId = receipt.getCustomerCard().getId();
        this.customerCardName = receipt.getCustomerCard().getName();
        this.customerCardNumber = receipt.getCustomerCard().getAccountNumber();
        this.employeeId = receipt.getEmployee().getId();
        this.employeeName = receipt.getEmployee().getName();
        this.branchId = receipt.getBranch().getId();
        this.branchName = receipt.getBranch().getName();
        this.totalFee = receipt.getBills().stream().mapToDouble(BillEntity::getFee).sum() + receipt.getShipmentFee();
        this.note = receipt.getNote();
        this.approverCode = receipt.getApproverCode();
    }
}
