package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SaveReceiptRepaymentEntryRequest extends SaveReceiptEntryRequest {
    @Min(
            value = 1000,
            message = "{msg.err.double.min}"
    )
    private int repaidAmount;

    @NotNull(message = "{msg.err.string.blank}")
    private String imageId;
}
