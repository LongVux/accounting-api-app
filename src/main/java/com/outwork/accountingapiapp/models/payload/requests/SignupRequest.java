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

    @NotBlank
    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.SHORT_STRING_MAX_LENGTH)
    private String name;

    @NotBlank
    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ENTITY_CODE_MAX_LENGTH)
    private String code;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = DataConstraint.DIGIT_ONLY_REGEX)
    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ID_STRING_MAX_LENGTH)
    private String phoneNumber;

    @NotBlank
    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.SHORT_STRING_MAX_LENGTH)
    private String password;

    @NotEmpty
    private List<UUID> roleIds;

    @NotEmpty
    private List<UUID> branchIds;

    public static UserEntity castToUserEntity (
            SignupRequest signUpRequest,
            List<RoleEntity> roleEntities,
            List<BranchEntity> branchEntities,
            PasswordEncoder passwordEncoder) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(signUpRequest.getName());
        userEntity.setCode(signUpRequest.getCode());
        userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userEntity.setEmail(signUpRequest.getEmail());
        userEntity.setPhoneNumber(signUpRequest.getPhoneNumber());
        userEntity.setRoles(roleEntities);
        userEntity.setBranches(branchEntities);

        return userEntity;
    }
}
