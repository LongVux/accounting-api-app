package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.RoleEntity;
import com.outwork.accountingapiapp.models.entity.UserEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetUserTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SignupRequest;
import com.outwork.accountingapiapp.models.payload.requests.UpdateUserRequest;
import com.outwork.accountingapiapp.models.payload.responses.UserTableItem;
import com.outwork.accountingapiapp.repositories.UserRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    public static final String ERROR_MSG_USER_CODE_EXISTED = "Mã nhân viên đã tồn tại";
    public static final String ERROR_MSG_EMAIL_EXISTED = "Email đã tồn tại";
    public static final String ERROR_MSG_PHONE_NAME_EXISTED = "Số điện thoại đã tồn tại";
    public static final String ERROR_MSG_BRANCH_BANK_ACCOUNT_EXISTED = "Tài khoản ngân hàng đã tồn tại";
    private String ERROR_MSG_SOME_ROLE_NOT_EXISTED = "Một số chức danh không tồn tại";
    private String ERROR_MSG_SOME_BRANCH_NOT_EXISTED = "Một số chi nhánh không tồn tại";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BranchService branchService;

    public Optional<UserEntity> findUserEntityByCode(String userCode) {
        return userRepository.findByCode(userCode);
    }

    public UserEntity getUserEntityByCode(String userCode) {
        return userRepository.findByCode(userCode).orElseThrow(() -> new EntityNotFoundException(userCode));
    }

    public Page<UserTableItem> getUserTableItems (GetUserTableItemRequest request) {
        return userRepository.findAll(request, request.retrievePageConfig()).map(UserTableItem::new);
    }

    public UserEntity getUserEntityById (@NotNull UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public UserEntity createUser (@Valid SignupRequest request, String encodedPassword) {
        UserEntity newUser = new UserEntity();

        List<RoleEntity> roleEntities = roleService.findRolesByIds(request.getRoleIds());
        List<BranchEntity> branchEntities = branchService.findBranchEntitiesByIds(request.getBranchIds());

        newUser.setName(request.getName());
        newUser.setCode(request.getCode());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setAccountNumber(request.getAccountNumber());
        newUser.setBank(request.getBank());
        newUser.setPassword(encodedPassword);
        newUser.setBranches(branchEntities);
        newUser.setRoles(roleEntities);

        validateSaveUserRequest(newUser, request.getRoleIds(), request.getBranchIds());

        return userRepository.save(newUser);
    }

    public UserEntity updateUser (@Valid UpdateUserRequest request, @NotNull UUID id) {
        UserEntity savedUser = getUserEntityById(id);

        List<RoleEntity> roleEntities = roleService.findRolesByIds(request.getRoleIds());
        List<BranchEntity> branchEntities = branchService.findBranchEntitiesByIds(request.getBranchIds());

        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
        savedUser.setPhoneNumber(request.getPhoneNumber());
        savedUser.setAccountNumber(request.getAccountNumber());
        savedUser.setBank(request.getBank());
        savedUser.setBranches(branchEntities);
        savedUser.setRoles(roleEntities);

        validateSaveUserRequest(savedUser, request.getRoleIds(), request.getBranchIds());

        return userRepository.save(savedUser);
    }

    public UserEntity saveUserEntity (UserEntity user) {
        validateSaveUserRequest(
                user,
                user.getRoles().stream().map(RoleEntity::getId).toList(),
                user.getBranches().stream().map(BranchEntity::getId).toList()
        );

        return userRepository.save(user);
    }

    private void validateSaveUserRequest (UserEntity user, List<UUID> roleIds, List<UUID> branchIds) {
        if (roleIds.size() > user.getRoles().size()) {
            throw new EntityNotFoundException(ERROR_MSG_SOME_ROLE_NOT_EXISTED);
        }

        if (branchIds.size() > user.getBranches().size()) {
            throw new EntityNotFoundException(ERROR_MSG_SOME_BRANCH_NOT_EXISTED);
        }

        if (userRepository.existsByEmailAndIdNot(user.getEmail(), Optional.ofNullable(user.getId()).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_EMAIL_EXISTED);
        }

        if (userRepository.existsByCodeAndIdNot(user.getCode(), Optional.ofNullable(user.getId()).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_USER_CODE_EXISTED);
        }

        if (userRepository.existsByPhoneNumberAndIdNot(user.getPhoneNumber(), Optional.ofNullable(user.getId()).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_PHONE_NAME_EXISTED);
        }

        if (userRepository.existsByAccountNumberAndBankIgnoreCaseAndIdNot(user.getAccountNumber(), user.getBank(), Optional.ofNullable(user.getId()).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_BRANCH_BANK_ACCOUNT_EXISTED);
        }
    }
}
