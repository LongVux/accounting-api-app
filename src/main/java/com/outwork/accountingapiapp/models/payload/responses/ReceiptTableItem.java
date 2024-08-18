package com.outwork.accountingapiapp.models.payload.responses;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import com.opencsv.bean.CsvNumber;
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

    @CsvBindByName(column = "A. Ngay tao")
    @CsvDate("dd.MM.yyyy HH:mm")
    private Date createdDate;

    @CsvBindByName(column = "B. Ma hoa don")
    private String code;

    @CsvBindByName(column = "C. Tong giao dich")
    @CsvNumber("#.##")
    private double transactionTotal;

    @CsvBindByName(column = "D. Tong phi bill")
    @CsvNumber("#.##")
    private double totalFee;

    @CsvBindByName(column = "E. Phi ship")
    @CsvNumber("#.##")
    private double shipmentFee;

    @CsvBindByName(column = "F. Thu")
    @CsvNumber("#.##")
    private double intake;

    @CsvBindByName(column = "G. Chi")
    @CsvNumber("#.##")
    private double payout;

    @CsvBindByName(column = "H. No")
    @CsvNumber("#.##")
    private double loan;

    @CsvBindByName(column = "I. Thu no")
    @CsvNumber("#.##")
    private double repayment;

    @CsvBindByName(column = "J. Loi nhuan uoc tinh")
    @CsvNumber("#.##")
    private double estimatedProfit;

    @CsvBindByName(column = "K. Loi nhuan thuc te")
    @CsvNumber("#.##")
    private double calculatedProfit;

    @CsvBindByName(column = "L. Ten the")
    private String customerCardName;

    @CsvBindByName(column = "M. So the", capture="([^ ]+) .*")
    private String customerCardNumber;

    @CsvBindByName(column = "N. Phan tram phi hoa don")
    @CsvNumber("#.##")
    private double percentageFee;

    @CsvBindByName(column = "O. Ma nhan vien")
    private String employeeName;

    @CsvBindByName(column = "P. Ma chi nhanh")
    private String branchName;

    @CsvBindByName(column = "Q. Ma quan ly xac nhan")
    private String approverCode;

    @CsvBindByName(column = "R. Ten khach hang")
    private String customerName;

    @CsvBindByName(column = "S. Ghi chu")
    private String note;

    @CsvIgnore
    private UUID id;

    @CsvIgnore
    private ReceiptStatusEnum receiptStatusEnum;

    @CsvIgnore
    private UUID customerCardId;

    @CsvIgnore
    private UUID employeeId;

    @CsvIgnore
    private UUID branchId;

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
        this.employeeName = receipt.getEmployee().getCode();
        this.branchId = receipt.getBranch().getId();
        this.branchName = receipt.getBranch().getCode();
        this.totalFee = receipt.getBills().stream().mapToDouble(BillEntity::getFee).sum() + receipt.getShipmentFee();
        this.note = receipt.getNote();
        this.approverCode = receipt.getApproverCode();
        this.customerName = receipt.getCustomerCard().getCustomer().getName();
    }
}
