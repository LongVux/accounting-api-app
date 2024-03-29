package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.utils.validator.DoubleStep;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SaveBranchAccountEntryRequest {
    @NotBlank(message = "{msg.err.string.blank}")
    private String entryType;

    @NotNull(message = "{msg.err.string.blank}")
    private TransactionTypeEnum transactionType;

    @Min(value = 1000, message = "{msg.err.double.min}")
    private double moneyAmount;

    @NotNull(message = "{msg.err.string.blank}")
    private String explanation;

    private String imageId;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID branchId;
}
