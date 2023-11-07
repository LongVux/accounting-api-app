package com.outwork.accountingapiapp.models.payload.responses;

import java.util.UUID;

/**
 * Projection for {@link com.outwork.accountingapiapp.models.entity.CardTypeEntity}
 */
public interface SuggestedCardType {
    UUID getId();

    String getName();
}