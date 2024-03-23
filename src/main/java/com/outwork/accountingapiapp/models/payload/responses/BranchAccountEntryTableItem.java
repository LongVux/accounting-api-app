package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class BranchAccountEntryTableItem {
    private UUID id;
    private Date createdDate;
    private String entryCode;
    private TransactionTypeEnum transactionType;
    private String entryType;
    private double moneyAmount;
    private AccountEntryStatusEnum entryStatus;
    private String branchCode;
    private String lastModifiedBy;
    private String note;

    public BranchAccountEntryTableItem (BranchAccountEntryEntity entry) {
        this.id = entry.getId();
        this.createdDate = entry.getCreatedDate();
        this.entryCode = entry.getEntryCode();
        this.transactionType = entry.getTransactionType();
        this.entryType = entry.getEntryType();
        this.moneyAmount = entry.getMoneyAmount();
        this.entryStatus = entry.getEntryStatus();
        this.branchCode = entry.getBranch().getCode();
        this.lastModifiedBy = entry.getLastModifiedBy();
        this.note = entry.getNote();
    }
}