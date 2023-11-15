package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.models.entity.RoleEntity;
import com.outwork.accountingapiapp.models.payload.requests.CreateRoleRequest;
import com.outwork.accountingapiapp.repositories.RoleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    public static final String ERROR_MSG_ROLE_TITLE_EXISTED = "Tên vai trò đã tồn tại";

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleEntity> findRolesByIds (List<UUID> roleIds) {
        return roleRepository.findByIdIn(roleIds);
    }

    public List<RoleEntity> getRoles () {
        return roleRepository.findAll();
    }

    public RoleEntity createRole (@NotNull @Valid CreateRoleRequest request) {
        if (roleRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new DuplicatedValueException(ERROR_MSG_ROLE_TITLE_EXISTED);
        }

        RoleEntity newRole = new RoleEntity();
        newRole.setTitle(request.getTitle());

        return roleRepository.save(newRole);
    }
}
