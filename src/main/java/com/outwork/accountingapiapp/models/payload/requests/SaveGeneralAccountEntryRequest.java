package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveGeneralAccountEntryRequest {
    @NotNull
    private String entryType;

    @NotNull
    private TransactionTypeEnum transactionType;

    @Min(1000)
    private double moneyAmount;

    @NotNull
    private String explanation;

    @NotNull
    private String imageId;
}
