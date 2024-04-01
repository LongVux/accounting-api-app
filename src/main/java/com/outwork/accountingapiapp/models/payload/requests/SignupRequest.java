package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.RoleEntity;
import com.outwork.accountingapiapp.models.entity.UserEntity;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@Data
public class SignupRequest {

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.SHORT_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String name;

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ENTITY_CODE_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    @Pattern(
            regexp = DataConstraint.CAPITAL_CHAR_AND_DIGIT_ONLY_REGEX,
            message = "{msg.err.string.regexp}"
    )
    private String code;

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

    @NotBlank
    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.SHORT_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String password;

    @NotEmpty(message = "{msg.err.list.empty}")
    private List<UUID> roleIds;

    @NotEmpty(message = "{msg.err.list.empty}")
    private List<SaveBranchManagementConfigRequest> saveBranchManagementConfigRequests;
}
