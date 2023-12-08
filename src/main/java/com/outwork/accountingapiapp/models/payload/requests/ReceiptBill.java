package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReceiptBill {

        private UUID billId;

        @NotNull(message = "{msg.err.string.blank}")
        private UUID posId;

        private double moneyAmount;

        private double fee;

        private double estimatedProfit;
}
