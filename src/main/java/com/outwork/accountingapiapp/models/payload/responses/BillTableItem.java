package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.constants.RecordStatusEnum;

import java.util.Date;
import java.util.UUID;

/**
 * Projection for {@link com.outwork.accountingapiapp.models.entity.BillEntity}
 */
public interface BillTableItem {
    String getCreatedBy();

    Date getCreatedDate();

    RecordStatusEnum getRecordStatusEnum();

    UUID getId();

    String getCode();

    long getTimeStampOrder();

    double getMoneyAmount();

    double getFee();

    double getEstimatedProfit();

    double getReturnedProfit();

    Date getReturnedTime();

    SuggestedPos getPos();

    SuggestedReceipt getReceipt();
}