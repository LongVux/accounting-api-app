package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.UserBranchEntity;
import com.outwork.accountingapiapp.models.entity.UserEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveBranchManagementConfigRequest;
import com.outwork.accountingapiapp.repositories.UserBranchRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserBranchService {
    public static final String ERROR_MSG_SOME_BRANCH_NOT_EXISTED = "Một số chi nhánh không tồn tại";
    @Autowired
    private UserBranchRepository userBranchRepository;

    @Autowired
    private BranchService branchService;

    public void buildUserBranchesForUser (List<SaveBranchManagementConfigRequest> requests, UserEntity user) {
        if (CollectionUtils.isEmpty(requests)) {
            return;
        }

        List<BranchEntity> branches = branchService.findBranchEntitiesByIds(requests.stream().map(SaveBranchManagementConfigRequest::getBranchId).toList());

        if (requests.size() > branches.size()) {
            throw new EntityNotFoundException(ERROR_MSG_SOME_BRANCH_NOT_EXISTED);
        }

        Map<UUID, UserBranchEntity> userBranchMap = new HashMap<>();

        for (int i = 0; i < requests.size(); i++) {
            UserBranchEntity newUserBranch = new UserBranchEntity();

            if (!ObjectUtils.isEmpty(user.getId())) {
                newUserBranch.setId(UUID.randomUUID());
                newUserBranch.setUser(user);
            }

            newUserBranch.setOrderId(i);
            userBranchMap.put(requests.get(i).getBranchId(), newUserBranch);
        }

        branches.forEach(branch -> {
            if (userBranchMap.containsKey(branch.getId())) {
                userBranchMap.get(branch.getId()).setBranch(branch);
            }
        });

        if (ObjectUtils.isEmpty(user.getBranchManagementScopes())) {
            user.setBranchManagementScopes(userBranchMap.values().stream().toList());
        } else {
            user.getBranchManagementScopes().clear();
            user.getBranchManagementScopes().addAll(userBranchMap.values().stream().toList());
        }
    }
}
