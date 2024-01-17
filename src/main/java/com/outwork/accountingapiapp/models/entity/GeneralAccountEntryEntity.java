package com.outwork.accountingapiapp.models.entity;

import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "general_account_entries")
public class GeneralAccountEntryEntity extends Auditable<String> {
    public static final String FIELD_ENTRY_CODE = "entryCode";
    public static final String FIELD_ENTRY_TYPE = "entryType";
    public static final String FIELD_ENTRY_STATUS = "entryStatus";
    public static final String FIELD_TRANSACTION_TYPE = "transactionType";
    public static final String FIELD_MONEY_AMOUNT = "moneyAmount";

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

    @Column(nullable = false)
    private double moneyAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountEntryStatusEnum entryStatus;

    @Column(nullable = false, updatable = false)
    private String explanation;

    private String imageId;
}