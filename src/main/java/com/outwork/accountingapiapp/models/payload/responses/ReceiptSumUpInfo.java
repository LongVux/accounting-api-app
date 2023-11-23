package com.outwork.accountingapiapp.models.payload.responses;

import lombok.Data;

@Data
public class ReceiptSumUpInfo {
    private double total;
    private double totalIntake;
    private double totalPayout;
    private double totalLoan;
    private double totalRepayment;
    private double totalEstimatedProfit;
    private double totalCalculatedProfit;
}
