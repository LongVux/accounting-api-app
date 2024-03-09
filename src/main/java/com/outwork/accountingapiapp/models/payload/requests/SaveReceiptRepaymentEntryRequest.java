package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.utils.validator.DoubleStep;
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
    @DoubleStep(value = 1000)
    private int repaidAmount;

    private String imageId;
}
