package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.utils.validator.DoubleStep;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
public class SaveReceiptRequest {

    private String imageId;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID branchId;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID customerCardId;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    @Max(
            value = 100,
            message = "{msg.err.double.max}"
    )
    private double percentageFee;

    @DoubleStep(value = 1000)
    private double shipmentFee;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    @DoubleStep(value = 1000)
    private double intake;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    @DoubleStep(value = 1000)
    private double payout;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    @DoubleStep(value = 1000)
    private double loan;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    @DoubleStep(value = 1000)
    private double repayment;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID employeeId;

    private List<@Valid ReceiptBill> receiptBills;

    @Size(
            max = 255,
            message = "{msg.err.string.range}"
    )
    private String note;

    private boolean usingCardPrePayFee;

    private boolean acceptExceededFee;
}
