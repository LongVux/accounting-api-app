package com.outwork.accountingapiapp.models.payload.requests;

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

    private int posCardFee;
}