package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "bills")
public class BillEntity extends Auditable<String> {
    public static final String FIELD_CODE = "code";
    public static final String FIELD_MONEY_AMOUNT = "moneyAmount";
    public static final String FIELD_FEE = "fee";
    public static final String FIELD_ESTIMATED_PROFIT = "estimatedProfit";
    public static final String FIELD_RETURNED_PROFIT = "returnedProfit";
    public static final String FIELD_RETURNED_TIME = "returnedTime";
    public static final String FIELD_POS = "pos";
    public static final String FIELD_RECEIPT = "receipt";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, updatable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String code;

    @Column(nullable = false, updatable = false)
    private long timeStampOrder;

    @Column(nullable = false)
    private double moneyAmount;

    @Column(nullable = false)
    private double fee;

    @Column(nullable = false)
    private double estimatedProfit;

    @Column(nullable = false)
    private double returnedProfit;

    @Temporal(TemporalType.TIMESTAMP)
    private Date returnedTime;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "posId", nullable = false)
    private PosEntity pos;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "receiptId", nullable = false)
    private ReceiptEntity receipt;

    public static BillEntity buildNewBill (ReceiptEntity receipt) {
        BillEntity bill = new BillEntity();

        if (!ObjectUtils.isEmpty(receipt.getId())) {
            bill.setId(UUID.randomUUID());
            bill.setReceipt(receipt);
        }
        return bill;
    }
}
