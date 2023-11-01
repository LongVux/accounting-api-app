package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveBranchRequest {

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.SHORT_STRING_MAX_LENGTH)
    private String name;

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ENTITY_CODE_MAX_LENGTH)
    @Pattern(regexp = DataConstraint.CAPITAL_CHAR_AND_DIGIT_ONLY_REGEX)
    private String code;

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ID_STRING_MAX_LENGTH)
    @Pattern(regexp = DataConstraint.DIGIT_ONLY_REGEX)
    private String phoneNumber;

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ID_STRING_MAX_LENGTH)
    private String accountNumber;

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ID_STRING_MAX_LENGTH)
    private String bank;
}
