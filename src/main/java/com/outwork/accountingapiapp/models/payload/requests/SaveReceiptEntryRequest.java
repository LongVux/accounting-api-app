package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SaveReceiptEntryRequest {
    @NotNull(message = "{msg.err.string.blank}")
    private UUID receiptId;

    @NotBlank(message = "{msg.err.string.blank}")
    private String explanation;

    private String imageId;
}
