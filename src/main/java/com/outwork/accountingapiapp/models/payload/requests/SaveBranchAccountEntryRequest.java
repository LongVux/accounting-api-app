package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SaveBranchAccountEntryRequest {
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

    @NotNull
    private UUID branchId;
}
