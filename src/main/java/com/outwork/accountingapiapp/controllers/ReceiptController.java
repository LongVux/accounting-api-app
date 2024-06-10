package com.outwork.accountingapiapp.controllers;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.payload.requests.*;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptSumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.ReceiptTableItem;
import com.outwork.accountingapiapp.services.BranchAccountEntryService;
import com.outwork.accountingapiapp.services.ReceiptService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @PutMapping("/note")
    public void saveNote (@RequestBody @Valid SaveNoteRequest request) {
        receiptService.saveReceiptNote(request);
    }

    @DeleteMapping("/{id}")
    public void deleteReceipt (@PathVariable @NotNull UUID id, @RequestParam(defaultValue = "") String explanation) {
        receiptService.deleteReceipt(id, explanation);
    }

    @GetMapping("/export")
    public void exportBills (@ModelAttribute @Valid GetReceiptTableItemRequest request, HttpServletResponse response) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        //set file name and content type
        String filename = "receipts.csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        //create a csv writer
        StatefulBeanToCsv<ReceiptTableItem> writer = new StatefulBeanToCsvBuilder<ReceiptTableItem>(response.getWriter())
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(false)
                .build();

        //write all users to csv file
        writer.write(receiptService.getAllReceiptTableItems(request));
    }
}
