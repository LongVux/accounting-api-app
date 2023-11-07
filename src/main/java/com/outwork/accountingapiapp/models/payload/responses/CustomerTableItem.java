package com.outwork.accountingapiapp.models.payload.responses;

import java.util.Date;
import java.util.UUID;

/**
 * Projection for {@link com.outwork.accountingapiapp.models.entity.CustomerEntity}
 */
public interface CustomerTableItem {
    Date getCreatedDate();

    UUID getId();

    String getName();

    String getAddress();

    String getPhoneNumber();

    String getNationalId();
}