package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.constants.ReceiptStatusEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "receipts")
public class ReceiptEntity extends Auditable<String> {
    public static final String FIELD_EMPLOYEE = "employee";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_CUSTOMER_CARD = "customerCard";
    public static final String FIELD_TRANSACTION_TOTAL = "transactionTotal";
    public static final String FIELD_INTAKE = "intake";
    public static final String FIELD_PAYOUT = "payout";
    public static final String FIELD_LOAN = "loan";
    public static final String FIELD_REPAYMENT = "repayment";
    public static final String FIELD_CALCULATED_PROFIT = "calculatedProfit";
    public static final String FIELD_ESTIMATED_PROFIT = "estimatedProfit";
    public static final String FIELD_RECEIPT_STATUS = "receiptStatus";
    public static final String FIELD_BRANCH = "branch";

    public static List<String> getSumUpFields () {
        return Arrays.asList(FIELD_TRANSACTION_TOTAL, FIELD_INTAKE, FIELD_PAYOUT, FIELD_LOAN, FIELD_REPAYMENT, FIELD_ESTIMATED_PROFIT, FIELD_CALCULATED_PROFIT);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private ReceiptStatusEnum receiptStatus;

    @Column(nullable = false)
    private double percentageFee;

    private double shipmentFee;

    @Column(nullable = false)
    private double intake;

    @Column(nullable = false)
    private double payout;

    @Column(nullable = false)
    private double loan;

    @Column(nullable = false)
    private double repayment;

    @Column(nullable = false)
    private double transactionTotal;

    @Column(nullable = false)
    private double calculatedProfit;

    @Column(nullable = false)
    private double estimatedProfit;

    private String imageId;

    private String note;

    private boolean usingCardPrePayFee;

    private boolean acceptExceededFee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customerCardId", nullable = false)
    private CustomerCardEntity customerCard;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee", nullable = false)
    private UserEntity employee;

    @OneToMany(mappedBy = "receipt", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<BillEntity> bills;

    @JsonIgnore
    @OneToMany(mappedBy = "receipt", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<BranchAccountEntryEntity> branchAccountEntries;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branchId", nullable = false)
    private BranchEntity branch;

    public void setBills(List<BillEntity> bills) {
        this.bills = bills.stream().peek(bill -> bill.setReceipt(this)).toList();
    }

    public void setBranchAccountEntries(List<BranchAccountEntryEntity> branchAccountEntries) {
        this.branchAccountEntries = branchAccountEntries.stream().peek(entry -> entry.setReceipt(this)).toList();
    }
}