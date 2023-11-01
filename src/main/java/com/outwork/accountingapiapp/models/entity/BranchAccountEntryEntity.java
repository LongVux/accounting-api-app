package com.outwork.accountingapiapp.models.entity;

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
    public BranchAccountEntryEntity (
            ReceiptEntity receipt,
            String explanation,
            TransactionTypeEnum transactionType,
            int moneyAmount
    ) {
        BranchAccountEntryEntity entry = new BranchAccountEntryEntity();
        entry.setEntryType(receipt.getCode());
        entry.setBranch(receipt.getBranch());
        entry.setReceipt(receipt);
        entry.setImageId(receipt.getImageId());
        entry.setExplanation(explanation);
        entry.setTransactionType(transactionType);
        entry.setMoneyAmount(moneyAmount);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String entryCode;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @Column(nullable = false)
    private String entryType;

    @Column(nullable = false, updatable = false)
    private int moneyAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountEntryStatusEnum entryStatus;

    @Column(nullable = false, updatable = false)
    private String explanation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branchId", nullable = false, updatable = false)
    private BranchEntity branch;

    @ManyToOne
    @JoinColumn(name = "receiptId", unique = true, updatable = false)
    private ReceiptEntity receipt;

    @Column(nullable = false)
    private String imageId;
}
