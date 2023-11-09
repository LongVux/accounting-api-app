package com.outwork.accountingapiapp.services;

import com.outwork.accountingapiapp.models.entity.AccountEntryTypeEntity;
import com.outwork.accountingapiapp.models.payload.responses.AccountEntryTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountEntryTypeService {
    @Autowired
    private AccountEntryTypeRepository accountEntryTypeRepository;

    public List<AccountEntryTypeEntity> getAll () {
        return accountEntryTypeRepository.findAll();
    }

    public AccountEntryTypeEntity getEntryTypeById (@NotNull UUID id) {
        return accountEntryTypeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public List<AccountEntryTypeEntity> findEntryType (@Size(min = 2) String searchKey) {
        return accountEntryTypeRepository.findByTitleContainsIgnoreCase(searchKey);
    }

    public AccountEntryTypeEntity createEntryType (@Size(min = 2) String title) {
        AccountEntryTypeEntity newEntryType = new AccountEntryTypeEntity();
        newEntryType.setTitle(title);

        return accountEntryTypeRepository.save(newEntryType);
    }

    public AccountEntryTypeEntity updateEntryType (@NotNull AccountEntryTypeEntity entryType) {
        return accountEntryTypeRepository.save(entryType);
    }

    public void deleteEntryType (@NotNull UUID id) {
        accountEntryTypeRepository.deleteById(id);
    }
}
