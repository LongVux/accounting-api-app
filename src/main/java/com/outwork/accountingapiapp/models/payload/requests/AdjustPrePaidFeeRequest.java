package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.utils.validator.DoubleStep;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AdjustPrePaidFeeRequest {

    @NotNull(message = "{msg.err.string.blank}")
    private UUID customerCardId;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    @DoubleStep(value = 1000)
    private int prePaidFee;

    private String imageId;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID branchId;
}
