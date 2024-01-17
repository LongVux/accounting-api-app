package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.AccountEntryTypeEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveAccountEntryType;
import com.outwork.accountingapiapp.models.payload.responses.AccountEntryTypeRepository;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedBranch;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AccountEntryTypeService {
    @Autowired
    private AccountEntryTypeRepository accountEntryTypeRepository;

    @Autowired
    private UserService userService;

    public List<AccountEntryTypeEntity> getAll () {
        return accountEntryTypeRepository.findAll();
    }

    public AccountEntryTypeEntity getEntryTypeById (@NotNull UUID id) {
        return accountEntryTypeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public List<String> findEntryType (@Size(min = 2) String searchKey, TransactionTypeEnum transactionType) {
        List<String> result = userService.searchUserCode(searchKey);

        if (ObjectUtils.isEmpty(transactionType)) {
            result.addAll(accountEntryTypeRepository.findByTitleContainsIgnoreCase(searchKey).stream().map(AccountEntryTypeEntity::getTitle).toList());
        } else {
            result.addAll(accountEntryTypeRepository.findByTitleContainsIgnoreCaseAndTransactionType(searchKey, transactionType).stream().map(AccountEntryTypeEntity::getTitle).toList());
        }

        return result;
    }

    public AccountEntryTypeEntity createEntryType (@Valid SaveAccountEntryType request) {
        AccountEntryTypeEntity newEntryType = new AccountEntryTypeEntity();
        newEntryType.setTitle(request.getTitle());
        newEntryType.setTransactionType(request.getTransactionType());

        return accountEntryTypeRepository.save(newEntryType);
    }

    public void deleteEntryType (@NotNull UUID id) {
        accountEntryTypeRepository.deleteById(id);
    }
}
