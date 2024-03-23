package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveBranchRequest;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedBranch;
import com.outwork.accountingapiapp.repositories.BranchRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BranchService {
    public static final String ERROR_MSG_BRANCH_NAME_EXISTED = "Tên chi nhánh đã tồn tại";
    public static final String ERROR_MSG_BRANCH_CODE_EXISTED = "Mã chi nhánh đã tồn tại";
    public static final String ERROR_MSG_BRANCH_PHONE_NUMBER_EXISTED = "Số điện thoại chi nhánh đã tồn tại";

    public static final String ERROR_MSG_CANNOT_DELETE = "Dữ liệu này đã được sử dụng trong hệ thống, không thể xóa!";

    @Autowired
    private BranchRepository branchRepository;

    public List<BranchEntity> findBranchEntitiesByIds (List<UUID> branchIds) {
        return branchRepository.findByIdIn(branchIds);
    }

    public List<SuggestedBranch> findBranchesByKeyCode (@Size(min = 2) String keyCode) {
        return branchRepository.findByCodeContainsIgnoreCase(keyCode);
    }

    public List<BranchEntity> getBranches () {
        return branchRepository.findAll();
    }

    public List<BranchEntity> getBranches (List<UUID> branchIds) {
        return branchRepository.findByIdIn(branchIds);
    }

    public BranchEntity getBranchById (@NotNull UUID id) {
        return branchRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Optional<BranchEntity> getBranchByCode (@NotNull String code) {
        return branchRepository.findByCode(code);
    }

    public BranchEntity saveBranch (@Valid SaveBranchRequest request, UUID id) {
        validateSaveBranchRequest(request, id);

        BranchEntity savedBranch = ObjectUtils.isEmpty(id) ? new BranchEntity() : getBranchById(id);
        mapSaveBranchRequestToEntity(request, savedBranch);

        return branchRepository.save(savedBranch);
    }

    public void deleteBranch (@NotNull UUID branchId) {
        try {
            branchRepository.deleteById(branchId);
        } catch (Exception e) {
            throw new InvalidDataException(ERROR_MSG_CANNOT_DELETE);
        }
    }


    public void mapSaveBranchRequestToEntity (SaveBranchRequest request, BranchEntity branch) {
        branch.setName(request.getName());
        branch.setCode(request.getCode());
        branch.setPhoneNumber(request.getPhoneNumber());
    }

    private void validateSaveBranchRequest (SaveBranchRequest request, UUID id) {
        if (branchRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), Optional.ofNullable(id).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_BRANCH_NAME_EXISTED);
        }

        if (branchRepository.existsByCodeIgnoreCaseAndIdNot(request.getCode(), Optional.ofNullable(id).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_BRANCH_CODE_EXISTED);
        }

        if (branchRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), Optional.ofNullable(id).orElse(UUID.randomUUID()))) {
            throw new DuplicatedValueException(ERROR_MSG_BRANCH_PHONE_NUMBER_EXISTED);
        }
    }
}
