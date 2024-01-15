package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetReceiptTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptEntryRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRepaymentEntryRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRequest;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptSumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptTableItem;
import com.outwork.accountingapiapp.services.BranchAccountEntryService;
import com.outwork.accountingapiapp.services.ReceiptService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private BranchAccountEntryService branchAccountEntryService;

    @GetMapping
    public ResponseEntity<Page<ReceiptTableItem>> getReceiptTableItems (@ModelAttribute @Valid GetReceiptTableItemRequest request) {
        return ResponseEntity.ok(receiptService.getReceiptTableItems(request));
    }

    @GetMapping("/sumUp")
    public ResponseEntity<ReceiptSumUpInfo> getReceiptSumUpInfo (@ModelAttribute @Valid GetReceiptTableItemRequest request) {
        return ResponseEntity.ok(receiptService.getReceiptSumUpInfo(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptEntity> getReceiptById (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(receiptService.getReceipt(id));
    }

    @PostMapping("/reCalculate/{id}")
    public ResponseEntity<ReceiptEntity> reCalculatedReceipt (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(receiptService.reCalculatedReceipt(id));
    }

    @PostMapping
    public ResponseEntity<ReceiptEntity> createReceipt (@RequestBody @Valid SaveReceiptRequest request) {
        return new ResponseEntity<>(receiptService.saveReceipt(request, null), HttpStatus.CREATED);
    }

    @PutMapping("/confirmReceipt")
    public ResponseEntity<ReceiptEntity> confirmReceipt (@RequestBody @Valid SaveReceiptEntryRequest request) {
        return ResponseEntity.ok(branchAccountEntryService.confirmReceiptEntry(request));
    }

    @PutMapping("/repayReceipt")
    public ResponseEntity<ReceiptEntity> repayReceipt (@RequestBody @Valid SaveReceiptRepaymentEntryRequest request) {
        return ResponseEntity.ok(branchAccountEntryService.confirmRepayReceipt(request));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ReceiptEntity> updateReceipt (@RequestBody @Valid SaveReceiptRequest request, @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(receiptService.saveReceipt(request, id));
    }

    @DeleteMapping("/{id}")
    public void deleteReceipt (@PathVariable @NotNull UUID id) {
        receiptService.deleteReceipt(id);
    }
}
