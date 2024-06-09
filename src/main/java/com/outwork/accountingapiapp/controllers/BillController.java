package com.outwork.accountingapiapp.controllers;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.outwork.accountingapiapp.models.entity.BillEntity;
import com.outwork.accountingapiapp.models.payload.requests.*;
import com.outwork.accountingapiapp.models.payload.responses.BillSumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.BillTableItem;
import com.outwork.accountingapiapp.services.BillService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping
    public ResponseEntity<Page<BillTableItem>> getBillTableItems (@ModelAttribute @Valid GetBillTableItemRequest request) {
        return ResponseEntity.ok(billService.getBillTableItems(request));
    }

    @GetMapping("/sumUp")
    public ResponseEntity<BillSumUpInfo> getBillSumUpInfo (@ModelAttribute @Valid GetBillTableItemRequest request) {
        return ResponseEntity.ok(billService.getBillSumUpInfo(request));
    }

    @GetMapping("/matchBills")
    public ResponseEntity<List<BillEntity>> getMatchingBills (@ModelAttribute @Valid GetMatchingBillsRequest request) {
        return ResponseEntity.ok(billService.getMatchingBills(request));
    }

    @GetMapping("/modifyPosFee")
    public ResponseEntity<List<BillEntity>> getPosFeeModifyingBills (@ModelAttribute @Valid GetPosFeeModifyingBillRequest request) {
        return ResponseEntity.ok(billService.getPosFeeModifyingBills(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillEntity> updatePosFeeForBills (@RequestBody @Valid UpdateBillRequest request, @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(billService.updateBill(request, id));
    }

    @PutMapping("/modifyPosFee")
    public ResponseEntity<List<BillEntity>> updatePosFeeForBills (@RequestBody @Valid UpdatePosFeeForBillsRequest request) {
        return ResponseEntity.ok(billService.updatePosFeeForBills(request));
    }

    @PutMapping("/matchBill")
    public ResponseEntity<List<BillEntity>> matchBills (@RequestBody @Valid MatchingBillRequest request) {
        return ResponseEntity.ok(billService.matchBill(request));
    }

    @PutMapping("/note")
    public void saveNote (@RequestBody @Valid SaveNoteRequest request) {
        billService.saveBillNote(request);
    }

    @GetMapping("/export")
    public void exportBills (@ModelAttribute @Valid GetBillTableItemRequest request, HttpServletResponse response) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        //set file name and content type
        String filename = "bills.csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        //create a csv writer
        StatefulBeanToCsv<BillTableItem> writer = new StatefulBeanToCsvBuilder<BillTableItem>(response.getWriter())
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(false)
                .build();

        //write all users to csv file
        writer.write(billService.getAllBillTableItems(request));
    }
}
