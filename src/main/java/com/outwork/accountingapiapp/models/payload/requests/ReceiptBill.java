package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.utils.validator.DoubleStep;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReceiptBill {

        private UUID billId;

        @NotNull(message = "{msg.err.string.blank}")
        private UUID posId;

        @Min(
                value = 1000,
                message = "{msg.err.double.min}"
        )
        @DoubleStep(value = 1000)
        private double moneyAmount;

//        @Min(
//                value = 1000,
//                message = "{msg.err.double.min}"
//        )
//        @DoubleStep(value = 1000)
        private double fee;
}
