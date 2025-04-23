package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.services.DataBackupService;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"accountNumber", "bank"})})
public class UserEntity {
    public static final String FIELD_ID = "id";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PHONE_NUMBER = "phoneNumber";
    public static final String FIELD_BRANCH_MANAGEMENT_SCOPES = "branchManagementScopes";
    public static final String FIELD_ROLES = "roles";
    public static final String FIELD_SALARY = "salary";
    public static final String FIELD_ACCOUNT_BALANCE = "accountBalance";

    public static final String ERROR_MSG_ACCOUNT_BALANCE_NOT_ENOUGH = "Số dư tài khoản không đủ để thực hiện hành động này";
    public static final String ERROR_MSG_ACCOUNT_BALANCE_BEING_INVALID = "Số dư tài khoản đang bị lỗi";

    private static final Logger log = LoggerFactory.getLogger(UserEntity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = DataConstraint.ENTITY_CODE_MAX_LENGTH)
    private String code;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column
    private Double salary;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String accountNumber;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String bank;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String accountBalance;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<RoleEntity> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBranchEntity> branchManagementScopes;

    public void setAccountBalance (double accountBalance) {
        if (accountBalance < 0) {
            throw new InvalidDataException(ERROR_MSG_ACCOUNT_BALANCE_NOT_ENOUGH);
        } else {
            this.accountBalance = encrypt(accountBalance);
        }
    }

    public double getAccountBalance () {
        return decrypt(accountBalance);
    }

    private String encrypt(double value) {
        try {
            if (ObjectUtils.isEmpty(value)) return null;
            String str = Double.toString(value);
            return Base64.getEncoder().encodeToString(str.getBytes());
        } catch (Exception e) {
            log.error("Failed to encrypt account balance of {} with value {}", this.code, value);
            log.error("Failed to encrypt account balance: ", e);
            throw new InvalidDataException(ERROR_MSG_ACCOUNT_BALANCE_BEING_INVALID);
        }

    }

    private double decrypt(String encrypted) {
        try {
            if (StringUtils.isBlank(encrypted)) return 0;
            String decoded = new String(Base64.getDecoder().decode(encrypted));
            return Double.parseDouble(decoded);
        } catch (Exception e) {
            log.error("Failed to decrypt account balance of {} with value {}", this.code, encrypted);
            log.error("Failed to decrypt account balance: ", e);
            throw new InvalidDataException(ERROR_MSG_ACCOUNT_BALANCE_BEING_INVALID);
        }
    }

//    public List<UserBranchEntity> getBranchManagementScopes () {
//        return this.branchManagementScopes;
//    }

    public UserBranchEntity getDefaultBranchManagementScopes () {
        List<UserBranchEntity> scopes = this.branchManagementScopes.stream().sorted(Comparator.comparingInt(UserBranchEntity::getOrderId)).toList();
        return scopes.get(0);
    }
}
