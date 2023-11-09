package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.exceptions.DuplicatedValueException;
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
import java.util.UUID;

@Service
public class BranchService {
    public static final String ERROR_MSG_BRANCH_NAME_EXISTED = "Tên chi nhánh đã tồn tại";
    public static final String ERROR_MSG_BRANCH_CODE_EXISTED = "Mã chi nhánh đã tồn tại";
    public static final String ERROR_MSG_BRANCH_PHONE_NUMBER_EXISTED = "Số điện thoại chi nhánh đã tồn tại";
    public static final String ERROR_MSG_BRANCH_BANK_ACCOUNT_EXISTED = "Tài khoản ngân hàng của chi nhánh đã tồn tại";

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

    public BranchEntity getBranchById (@NotNull UUID id) {
        return branchRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public BranchEntity saveBranch (@Valid SaveBranchRequest request, UUID id) {
        validateSaveBranchRequest(request, id);

        BranchEntity savedBranch = ObjectUtils.isEmpty(id) ? new BranchEntity() : getBranchById(id);
        mapSaveBranchRequestToEntity(request, savedBranch);

        return branchRepository.save(savedBranch);
    }

    public void mapSaveBranchRequestToEntity (SaveBranchRequest request, BranchEntity branch) {
        branch.setName(request.getName());
        branch.setCode(request.getCode());
        branch.setPhoneNumber(request.getPhoneNumber());
        branch.setAccountNumber(request.getAccountNumber());
        branch.setBank(request.getBank());
    }

    private void validateSaveBranchRequest (SaveBranchRequest request, UUID id) {
        if (branchRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new DuplicatedValueException(ERROR_MSG_BRANCH_NAME_EXISTED);
        }

        if (branchRepository.existsByCodeIgnoreCaseAndIdNot(request.getCode(), id)) {
            throw new DuplicatedValueException(ERROR_MSG_BRANCH_CODE_EXISTED);
        }

        if (branchRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), id)) {
            throw new DuplicatedValueException(ERROR_MSG_BRANCH_PHONE_NUMBER_EXISTED);
        }

        if (branchRepository.existsByAccountNumberAndBankIgnoreCaseAndIdNot(request.getAccountNumber(), request.getBank(), id)) {
            throw new DuplicatedValueException(ERROR_MSG_BRANCH_BANK_ACCOUNT_EXISTED);
        }
    }
}
