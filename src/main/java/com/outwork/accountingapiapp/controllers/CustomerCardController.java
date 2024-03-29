package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.CustomerCardEntity;
import com.outwork.accountingapiapp.models.payload.requests.AdjustPrePaidFeeRequest;
import com.outwork.accountingapiapp.models.payload.requests.GetCustomerCardTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveCustomerCardRequest;
import com.outwork.accountingapiapp.models.payload.responses.CustomerCardTableItem;
import com.outwork.accountingapiapp.services.CustomerCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customerCards")
public class CustomerCardController {

    @Autowired
    private CustomerCardService customerCardService;

    @GetMapping
    public ResponseEntity<Page<CustomerCardTableItem>> getCustomerCardTableItems (@ModelAttribute @Valid GetCustomerCardTableItemRequest request) {
        return ResponseEntity.ok(customerCardService.getCustomerCardTableItems(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerCardEntity> getCustomerCardById (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(customerCardService.getCustomerCardById(id));
    }

    @GetMapping("/findNonExpiredByCustomerId/{customerId}")
    public ResponseEntity<List<CustomerCardEntity>> getCustomerCardByCustomerId (@PathVariable @NotNull UUID customerId) {
        return ResponseEntity.ok(customerCardService.findNonExpiredCustomerCardByCustomerId(customerId));
    }

    @GetMapping("/findNonExpiredByCardNumber/{searchKey}")
    public ResponseEntity<List<CustomerCardEntity>> getCustomerCardByCustomerId (@PathVariable @NotNull @Size(min = 4) String searchKey) {
        return ResponseEntity.ok(customerCardService.findNonExpiredCustomerCardByCardNumber(searchKey));
    }

    @PostMapping
    public ResponseEntity<CustomerCardEntity> createCustomerCard (@RequestBody @Valid SaveCustomerCardRequest request) {
        return new ResponseEntity<>(customerCardService.saveCustomerCard(request, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerCardEntity> updateCustomerCard (@RequestBody @Valid SaveCustomerCardRequest request, @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(customerCardService.saveCustomerCard(request, id));
    }

    @PutMapping("/adjustPrePaidFee")
    public ResponseEntity<CustomerCardEntity> updatePrePaidFee (@RequestBody @Valid AdjustPrePaidFeeRequest request) {
        return ResponseEntity.ok(customerCardService.adjustPrePaidFee(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomerCard (@PathVariable @NotNull UUID id) {
        customerCardService.deleteCustomerCard(id);
        return ResponseEntity.ok().build();
    }
}
