package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateBillRequest {
    private UUID posId;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    @Max(
            value = 100,
            message = "{msg.err.double.max}"
    )
    private double posFeeStamp;
}
