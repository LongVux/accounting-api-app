package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MatchingBillRequest {
    @NotNull
    private List<UUID> billIds;
    private double moneyAmount;
    private boolean bypassDifference;
}
