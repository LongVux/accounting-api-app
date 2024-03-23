package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "branch_account_entries")
public class BranchAccountEntryEntity extends Auditable<String> {
    public static final String FIELD_ENTRY_CODE = "entryCode";
    public static final String FIELD_ENTRY_TYPE = "entryType";
    public static final String FIELD_BRANCH = "branch";
    public static final String FIELD_ENTRY_STATUS = "entryStatus";
    public static final String FIELD_TRANSACTION_TYPE = "transactionType";
    public static final String FIELD_MONEY_AMOUNT = "moneyAmount";

    public static BranchAccountEntryEntity createSystemBranchAccountEntry (
            ReceiptEntity receipt,
            String explanation,
            TransactionTypeEnum transactionType,
            double moneyAmount,
            String imageId
    ) {
        BranchAccountEntryEntity entry = new BranchAccountEntryEntity();
        entry.setEntryType(receipt.getCode());
        entry.setBranch(receipt.getBranch());
        entry.setReceipt(receipt);
        entry.setImageId(imageId);
        entry.setExplanation(explanation);
        entry.setTransactionType(transactionType);
        entry.setMoneyAmount(moneyAmount);
        entry.setEntryStatus(AccountEntryStatusEnum.APPROVED);

        return entry;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String entryCode;

    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @Column(nullable = false)
    private String entryType;

    @Column(nullable = false)
    private double moneyAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountEntryStatusEnum entryStatus;

    @Column(nullable = false)
    private String explanation;

    private String note;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branchId", nullable = false)
    private BranchEntity branch;

    @Column(updatable = false)
    private Long timeStampSeq;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "receiptId")
    private ReceiptEntity receipt;

    private String imageId;
}
