package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SaveReceiptRepaymentEntryRequest extends SaveReceiptEntryRequest {
    private int repaidAmount;

    @NotNull
    private String imageId;
}
