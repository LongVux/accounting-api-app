package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.RoleEntity;
import com.outwork.accountingapiapp.models.entity.UserEntity;
import lombok.Data;

import java.util.UUID;

@Data
public class UserTableItem {
    private UUID id;
    private String name;
    private String code;
    private String email;
    private String phoneNumber;
    private String branchCode;
    private String roleTitle;
    private Double accountBalance;

    public UserTableItem(UserEntity user) {
        this.id = user.getId();
        this.name = user.getName();
        this.code = user.getCode();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.branchCode = String.join(DataFormat.DEFAULT_SEPARATOR, user.getBranches().stream().map(BranchEntity::getCode).toList());
        this.roleTitle = String.join(DataFormat.DEFAULT_SEPARATOR, user.getRoles().stream().map(RoleEntity::getTitle).toList());
        this.accountBalance = user.getAccountBalance();
    }
}
