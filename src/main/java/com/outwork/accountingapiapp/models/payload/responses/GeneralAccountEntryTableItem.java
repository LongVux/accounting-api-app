package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.GeneralAccountEntryEntity;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class GeneralAccountEntryTableItem {
    private UUID id;
    private Date createdDate;
    private String entryCode;
    private TransactionTypeEnum transactionType;
    private String entryType;
    private double moneyAmount;
    private AccountEntryStatusEnum entryStatus;
    private String note;

    public GeneralAccountEntryTableItem(GeneralAccountEntryEntity entry) {
        this.id = entry.getId();
        this.createdDate = entry.getCreatedDate();
        this.entryCode = entry.getEntryCode();
        this.transactionType = entry.getTransactionType();
        this.entryType = entry.getEntryType();
        this.moneyAmount = entry.getMoneyAmount();
        this.entryStatus = entry.getEntryStatus();
        this.note = entry.getNote();
    }
}
