package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class GetMatchingBillsRequest {
    private double moneyAmount;

    @NotNull
    private UUID posId;

    @NotNull
    private Date fromCreatedDate;

    @NotNull
    private Date toCreatedDate;
}
