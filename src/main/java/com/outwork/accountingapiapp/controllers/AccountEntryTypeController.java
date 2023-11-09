package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.AccountEntryTypeEntity;
import com.outwork.accountingapiapp.services.AccountEntryTypeService;
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
    public ResponseEntity<List<AccountEntryTypeEntity>> findEntryTypeByTitle (@PathVariable @Size(min = 2) String title) {
        return ResponseEntity.ok(accountEntryTypeService.findEntryType(title));
    }

    @PostMapping
    public ResponseEntity<AccountEntryTypeEntity> createEntryType (@Size(min = 2) String title) {
        return new ResponseEntity<>(accountEntryTypeService.createEntryType(title), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<AccountEntryTypeEntity> updateEntryType (@RequestBody @NotNull AccountEntryTypeEntity entryType) {
        return ResponseEntity.ok(accountEntryTypeService.updateEntryType(entryType));
    }

    @DeleteMapping("/{id}")
    public void deleteEntryType (@PathVariable @NotNull UUID id) {
        accountEntryTypeService.deleteEntryType(id);
    }
}
