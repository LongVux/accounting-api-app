package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.utils.Util;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "bills")
public class BillEntity extends Auditable<String> {
    public static final String FIELD_CODE = "code";
    public static final String FIELD_MONEY_AMOUNT = "moneyAmount";
    public static final String FIELD_FEE = "fee";
    public static final String FIELD_POS_FEE_STAMP = "posFeeStamp";
    public static final String FIELD_RETURN_FROM_BANK = "returnFromBank";
    public static final String FIELD_RETURNED_TIME = "returnedTime";
    public static final String FIELD_POS = "pos";
    public static final String FIELD_RECEIPT = "receipt";

    public static List<String> getSumUpFields () {
        return Arrays.asList(FIELD_MONEY_AMOUNT, FIELD_RETURN_FROM_BANK);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String code;

    @Column(nullable = false, updatable = false)
    private long timeStampSeq;

    @Column(nullable = false)
    private double moneyAmount;

    @Column(nullable = false)
    private double fee;

    @Column(nullable = false)
    private double posFeeStamp;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private double returnFromBank;

    @Temporal(TemporalType.TIMESTAMP)
    private Date returnedTime;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "posId", nullable = false)
    private PosEntity pos;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "receiptId", nullable = false)
    private ReceiptEntity receipt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedDate;

    @Column(columnDefinition = "LONGTEXT")
    private String note;

    @Column(columnDefinition = "LONGTEXT")
    private String history;

    public static BillEntity buildNewBill (ReceiptEntity receipt) {
        BillEntity bill = new BillEntity();

        if (!ObjectUtils.isEmpty(receipt.getId())) {
            bill.setId(UUID.randomUUID());
        }

        bill.setReceipt(receipt);

        return bill;
    }

    public void setReturnFromBank (double returnFromBank) {
        this.returnFromBank = Util.numberStandardRound(returnFromBank);
    }

    public double getEstimatedReturnFromBank () {
        return Util.numberStandardRound(this.getMoneyAmount()*(1 - this.getPosFeeStamp() / 100));
    }
}
