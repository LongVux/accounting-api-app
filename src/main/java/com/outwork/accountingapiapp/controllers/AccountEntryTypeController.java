package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.AccountEntryTypeEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveAccountEntryType;
import com.outwork.accountingapiapp.services.AccountEntryTypeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/entryTypes")
public class AccountEntryTypeController {
    @Autowired
    private AccountEntryTypeService accountEntryTypeService;

    @GetMapping
    public ResponseEntity<List<AccountEntryTypeEntity>> getAll () {
        return ResponseEntity.ok(accountEntryTypeService.getAll());
    }

    @GetMapping("/findByTitle/{title}")
    public ResponseEntity<List<String>> findEntryTypeByTitle (@PathVariable @Size(min = 2) String title, @RequestParam TransactionTypeEnum transactionType) {
        return ResponseEntity.ok(accountEntryTypeService.findEntryType(title, transactionType));
    }

    @PostMapping
    public ResponseEntity<AccountEntryTypeEntity> createEntryType (@RequestBody @Valid SaveAccountEntryType request) {
        return new ResponseEntity<>(accountEntryTypeService.createEntryType(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteEntryType (@PathVariable @NotNull UUID id) {
        accountEntryTypeService.deleteEntryType(id);
    }
}
