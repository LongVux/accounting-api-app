package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveCustomerRequest {

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ID_STRING_MAX_LENGTH)
    private String name;

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.SHORT_STRING_MAX_LENGTH)
    private String address;

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ID_STRING_MAX_LENGTH)
    @Pattern(regexp = DataConstraint.DIGIT_ONLY_REGEX)
    private String phoneNumber;

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ID_STRING_MAX_LENGTH)
    private String nationalId;

    private int percentageFee;
}
