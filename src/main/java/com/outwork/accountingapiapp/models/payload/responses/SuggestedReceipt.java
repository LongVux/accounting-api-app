package com.outwork.accountingapiapp.models.payload.responses;

import java.util.UUID;

/**
 * Projection for {@link com.outwork.accountingapiapp.models.entity.ReceiptEntity}
 */
public interface SuggestedReceipt {
    UUID getId();

    String getCode();
}