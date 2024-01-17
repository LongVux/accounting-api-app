package com.outwork.accountingapiapp.models.payload.responses;

import lombok.Data;

@Data
public class BillSumUpInfo {
    private Double totalMoneyAmount;
    private Double totalEstimatedReturnFromBank;
    private Double totalReturnFromBank;
}
