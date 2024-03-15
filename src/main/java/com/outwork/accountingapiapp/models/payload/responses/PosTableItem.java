package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.constants.PosStatusEnum;
import com.outwork.accountingapiapp.models.entity.BranchEntity;

import java.util.UUID;

/**
 * Projection for {@link com.outwork.accountingapiapp.models.entity.PosEntity}
 */
public interface PosTableItem {
    UUID getId();

    String getCode();

    String getName();

    PosStatusEnum getPosStatus();

    String getAddress();

    String getAccountNumber();

    String getBank();

    String getNote();

    int getMaxBillAmount();

    BranchEntity getBranch();
}