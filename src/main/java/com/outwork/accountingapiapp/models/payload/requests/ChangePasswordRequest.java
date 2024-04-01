package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank
    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.SHORT_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String password;
}
