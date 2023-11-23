package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;

import java.util.Date;
import java.util.UUID;

public interface GeneralAccountEntryTableItem {
    Date getCreatedDate();

    UUID getId();

    String getEntryCode();

    TransactionTypeEnum getTransactionType();

    String getEntryType();

    double getMoneyAmount();

    AccountEntryStatusEnum getEntryStatus();
}
