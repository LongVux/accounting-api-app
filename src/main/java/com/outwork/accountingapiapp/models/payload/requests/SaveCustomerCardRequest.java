package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

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

    private int paymentLimit;

    private Date paymentDueDate;

    @NotNull(message = "{msg.err.string.blank}")
    private UUID customerId;
}
