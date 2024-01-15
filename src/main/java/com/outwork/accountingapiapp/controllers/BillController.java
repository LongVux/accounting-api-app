package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.BillEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetBillTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.GetMatchingBillsRequest;
import com.outwork.accountingapiapp.models.payload.requests.MatchingBillRequest;
import com.outwork.accountingapiapp.models.payload.responses.BillSumUpInfo;
import com.outwork.accountingapiapp.models.payload.responses.BillTableItem;
import com.outwork.accountingapiapp.services.BillService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/matchBill")
    public ResponseEntity<List<BillEntity>> matchBills (@RequestBody @Valid MatchingBillRequest request) {
        return ResponseEntity.ok(billService.matchBill(request));
    }
}
