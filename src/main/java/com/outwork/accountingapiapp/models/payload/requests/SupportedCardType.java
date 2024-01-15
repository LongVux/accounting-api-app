package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class SupportedCardType {
    private UUID id;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID cardTypeId;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    @Max(
            value = 100,
            message = "{msg.err.double.max}"
    )
    private double posCardFee;
}