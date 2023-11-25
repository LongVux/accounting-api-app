package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetBranchAccountEntryTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveBranchAccountEntryRequest;
import com.outwork.accountingapiapp.models.payload.responses.AccountEntrySumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.BranchAccountEntryTableItem;
import com.outwork.accountingapiapp.services.BranchAccountEntryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/branchAccountEntries")
public class BranchAccountEntryController {
    @Autowired
    private BranchAccountEntryService branchAccountEntryService;

    @GetMapping
    public ResponseEntity<Page<BranchAccountEntryTableItem>> getBranchAccountEntryTableItems (@ModelAttribute GetBranchAccountEntryTableItemRequest request) {
        return ResponseEntity.ok(branchAccountEntryService.getBranchAccountEntryTableItems(request));
    }

    @GetMapping("/sumUp")
    public ResponseEntity<AccountEntrySumUpInfo> getBranchAccountEntrySumUpInfo (@ModelAttribute GetBranchAccountEntryTableItemRequest request) {
        return ResponseEntity.ok(branchAccountEntryService.getBranchAccountEntrySumUpInfo(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchAccountEntryEntity> getBranchAccountEntryById (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(branchAccountEntryService.getEntryById(id));
    }

    @PostMapping
    public ResponseEntity<BranchAccountEntryEntity> createEntry (@RequestBody @Valid SaveBranchAccountEntryRequest request) {
        return new ResponseEntity<>(branchAccountEntryService.saveEntry(request, null), HttpStatus.CREATED);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<BranchAccountEntryEntity> approveEntry (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(branchAccountEntryService.approveEntry(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchAccountEntryEntity> updateEntry (@RequestBody @Valid SaveBranchAccountEntryRequest request, @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(branchAccountEntryService.saveEntry(request, id));
    }

    @DeleteMapping("/{id}")
    public void deleteEntry (@PathVariable @NotNull UUID id) {
        branchAccountEntryService.deleteBranchAccountEntry(id);
    }
}
