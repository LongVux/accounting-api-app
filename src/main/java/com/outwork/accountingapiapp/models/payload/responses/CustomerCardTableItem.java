package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.models.payload.responses.SuggestedCardType;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedCustomer;

import java.util.Date;
import java.util.UUID;

/**
 * Projection for {@link com.outwork.accountingapiapp.models.entity.CustomerCardEntity}
 */
public interface CustomerCardTableItem {
    Date getCreatedDate();

    UUID getId();

    String getName();

    String getAccountNumber();

    String getBank();

    String getNationalId();

    int getPaymentLimit();

    int getPaymentDueDate();

    SuggestedCardType getCardType();

    SuggestedCustomer getCustomer();
}