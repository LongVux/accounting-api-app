package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveCustomerRequest {

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ID_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String name;

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.SHORT_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String address;

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ID_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    @Pattern(
            regexp = DataConstraint.DIGIT_ONLY_REGEX,
            message = "{msg.err.string.regexp}"
    )
    private String phoneNumber;

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ID_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String nationalId;

    @Min(
            value = 0,
            message = "{msg.err.double.min}"
    )
    @Max(
            value = 100,
            message = "{msg.err.double.max}"
    )
    private double percentageFee;

    @Size(
            max = 255,
            message = "{msg.err.string.range}"
    )
    private String note;
}
