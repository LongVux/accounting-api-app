package com.outwork.accountingapiapp.models.payload.responses;

import lombok.Data;

@Data
public class AccountEntrySumUpInfo {
    private double total;
    private double totalIntake;
    private double totalPayout;
    private double totalLoan;
    private double totalRepayment;
}
