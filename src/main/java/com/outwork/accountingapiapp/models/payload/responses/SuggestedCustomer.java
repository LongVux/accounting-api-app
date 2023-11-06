package com.outwork.accountingapiapp.models.payload.responses;

import java.util.UUID;

/**
 * Projection for {@link com.outwork.accountingapiapp.models.entity.CustomerEntity}
 */
public interface SuggestedCustomer {
    UUID getId();

    String getName();
}