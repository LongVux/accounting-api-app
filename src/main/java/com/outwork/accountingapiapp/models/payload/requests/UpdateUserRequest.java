package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateUserRequest {
    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.SHORT_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String name;

    @NotBlank(message = "{msg.err.string.blank}")
    @Email(message = "{msg.err.string.email}")
    private String email;

    @Pattern(
            regexp = DataConstraint.DIGIT_ONLY_REGEX,
            message = "{msg.err.string.regexp}"
    )
    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ID_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String phoneNumber;

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

    private double salary;

    @NotEmpty(message = "{msg.err.list.empty}")
    private List<UUID> roleIds;

    @NotEmpty(message = "{msg.err.list.empty}")
    private List<SaveBranchManagementConfigRequest> saveBranchManagementConfigRequests;
}
