package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
public class SaveReceiptRequest {

    @NotNull
    private String imageId;

    @NotNull
    private UUID branchId;

    @NotNull
    private UUID customerCardId;

    @Min(0)
    private double percentageFee;

    @Min(0)
    private double shipmentFee;

    @Min(0)
    private double intake;

    @Min(0)
    private double payout;

    @Min(0)
    private double loan;

    @Min(0)
    private double repayment;

    @NotNull
    private UUID employeeId;

    private List<@Valid ReceiptBill> receiptBills;

}
