package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.GeneralAccountEntryEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetGeneralAccountEntryTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveGeneralAccountEntryRequest;
import com.outwork.accountingapiapp.models.payload.responses.AccountEntrySumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.GeneralAccountEntryTableItem;
import com.outwork.accountingapiapp.services.GeneralAccountEntryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/generalAccountEntries")
public class GeneralAccountEntryController {
    @Autowired
    private GeneralAccountEntryService generalAccountEntryService;

    @GetMapping
    public ResponseEntity<Page<GeneralAccountEntryTableItem>> getBranchAccountEntryTableItems (@ModelAttribute GetGeneralAccountEntryTableItemRequest request) {
        return ResponseEntity.ok(generalAccountEntryService.getBranchAccountEntryTableItems(request));
    }

    @GetMapping("/sumUp")
    public ResponseEntity<AccountEntrySumUpInfo> getBranchAccountEntrySumUpInfo (@ModelAttribute GetGeneralAccountEntryTableItemRequest request) {
        return ResponseEntity.ok(generalAccountEntryService.getGeneralAccountEntrySumUpInfo(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralAccountEntryEntity> getGeneralAccountEntryById (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(generalAccountEntryService.getEntryById(id));
    }

    @PostMapping
    public ResponseEntity<GeneralAccountEntryEntity> createEntry (@RequestBody @Valid SaveGeneralAccountEntryRequest request) {
        return new ResponseEntity<>(generalAccountEntryService.saveEntry(request, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralAccountEntryEntity> updateEntry (@RequestBody @Valid SaveGeneralAccountEntryRequest request, @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(generalAccountEntryService.saveEntry(request, id));
    }

    @DeleteMapping("/{id}")
    public void deleteEntry (@PathVariable @NotNull UUID id) {
        generalAccountEntryService.deleteBranchAccountEntry(id);
    }

}
