package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.payload.requests.GetBillTableItemRequest;
import com.outwork.accountingapiapp.models.payload.responses.BillTableItem;
import com.outwork.accountingapiapp.services.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping
    public ResponseEntity<Page<BillTableItem>> getBillTableItems (@ModelAttribute GetBillTableItemRequest request) {
        return ResponseEntity.ok(billService.getBillTableItems(request));
    }
}
