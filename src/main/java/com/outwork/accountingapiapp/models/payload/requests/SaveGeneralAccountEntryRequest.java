package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveGeneralAccountEntryRequest {
    @NotNull(message = "{msg.err.string.blank}")
    private String entryType;

    @NotNull(message = "{msg.err.string.blank}")
    private TransactionTypeEnum transactionType;

    @Min(value = 1000, message = "{msg.err.double.min}")
    private double moneyAmount;

    @NotNull
    private String explanation;

    @NotNull
    private String imageId;
}
