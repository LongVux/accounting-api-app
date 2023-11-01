package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SaveReceiptEntryRequest {
    @NotNull
    private UUID receiptId;

    @NotNull
    private String explanation;
}
