package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.CustomerEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveCustomerRequest;
import com.outwork.accountingapiapp.services.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{id}")
    public ResponseEntity<CustomerEntity> getCustomerById (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
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
