package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReceiptBill {

        private UUID billId;

        @NotNull
        private UUID posId;

        private int moneyAmount;

        private int fee;

        private double estimatedProfit;
}
