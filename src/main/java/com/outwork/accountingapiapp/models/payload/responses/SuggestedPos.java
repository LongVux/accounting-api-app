package com.outwork.accountingapiapp.models.payload.responses;

import java.util.UUID;

/**
 * Projection for {@link com.outwork.accountingapiapp.models.entity.PosEntity}
 */
public interface SuggestedPos {
    UUID getId();

    String getCode();

    int getMaxBillAmount();
}