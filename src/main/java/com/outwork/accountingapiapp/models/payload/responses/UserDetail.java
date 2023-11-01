package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.RoleEntity;
import com.outwork.accountingapiapp.models.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDetail {
    private UUID id;
    private String name;
    private String code;
    private String email;
    private String phoneNumber;
    private String token;
    private List<RoleEntity> roles;
    private List<BranchEntity> branches;

    public static UserDetail toUserDetail(UserEntity userEntity) {
        return new UserDetail(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getCode(),
                userEntity.getEmail(),
                userEntity.getPhoneNumber(),
                null,
                userEntity.getRoles(),
                userEntity.getBranches()
        );
    }
    public static UserDetail toUserDetail(UserEntity userEntity, String token) {
        return new UserDetail(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getCode(),
                userEntity.getEmail(),
                userEntity.getPhoneNumber(),
                token,
                userEntity.getRoles(),
                userEntity.getBranches()
        );
    }
}
