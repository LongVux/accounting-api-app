package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.utils.validator.DoubleStep;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

@Data
public class SaveCustomerCardRequest {

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ID_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String name;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID cardTypeId;

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ID_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String accountNumber;

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ID_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String bank;

    @Min(
            value = 1000,
            message = "{msg.err.double.min}"
    )
    @DoubleStep(value = 1000)
    private int paymentLimit;

    @Min(
            value = 1,
            message = "{msg.err.double.min}"
    )
    @Max(
            value = 31,
            message = "{msg.err.double.max}"
    )
    private int paymentDueDate;

    @NotNull(message = "{msg.err.string.blank}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date expiredDate;

    @Size(
            max = 255,
            message = "{msg.err.string.range}"
    )
    private String note;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID customerId;
}
