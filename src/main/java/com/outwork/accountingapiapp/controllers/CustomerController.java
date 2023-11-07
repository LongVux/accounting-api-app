package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.CustomerEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetCustomerTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SaveCustomerRequest;
import com.outwork.accountingapiapp.models.payload.responses.CustomerTableItem;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedCustomer;
import com.outwork.accountingapiapp.services.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<Page<CustomerTableItem>> getCustomerTableItems (@ModelAttribute GetCustomerTableItemRequest request) {
        return ResponseEntity.ok(customerService.getCustomerTableItems(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerEntity> getCustomerById (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<List<SuggestedCustomer>> findCustomersByName (@PathVariable @NotNull String name) {
        return ResponseEntity.ok(customerService.findCustomersByName(name));
    }

    @PostMapping
    public ResponseEntity<CustomerEntity> createCustomer (@RequestBody @Valid SaveCustomerRequest request) {
        return new ResponseEntity<>(customerService.saveCustomerEntity(request, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerEntity> updateCustomer (@RequestBody @Valid SaveCustomerRequest request, @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(customerService.saveCustomerEntity(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer (@PathVariable @NotNull UUID id) {
        customerService.deleteCustomerEntity(id);
        return ResponseEntity.ok().build();
    }
}
