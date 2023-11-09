package com.outwork.accountingapiapp.models.payload.responses;

import java.util.UUID;

/**
 * Projection for {@link com.outwork.accountingapiapp.models.entity.BranchEntity}
 */
public interface SuggestedBranch {
    UUID getId();

    String getName();

    String getCode();
}