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

    @NotNull(message = "{msg.err.string.blank}")
    private String imageId;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID branchId;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID customerCardId;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    private double percentageFee;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    private double shipmentFee;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    private double intake;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    private double payout;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    private double loan;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    private double repayment;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID employeeId;

    private List<@Valid ReceiptBill> receiptBills;

}
