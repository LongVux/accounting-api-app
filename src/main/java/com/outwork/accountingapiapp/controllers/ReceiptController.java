package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetReceiptTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveReceiptRequest;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptTableItem;
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

    @GetMapping("/")
    public ResponseEntity<Page<ReceiptTableItem>> getReceiptTableItems (@ModelAttribute GetReceiptTableItemRequest request) {
        return ResponseEntity.ok(receiptService.getReceiptTableItems(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptEntity> getReceiptById (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(receiptService.getReceipt(id));
    }

    @PostMapping
    public ResponseEntity<ReceiptEntity> createReceipt (@RequestBody @Valid SaveReceiptRequest request) {
        return new ResponseEntity<>(receiptService.saveReceipt(request, null), HttpStatus.CREATED);
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
