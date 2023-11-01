package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveBranchRequest;
import com.outwork.accountingapiapp.services.BranchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping
    public ResponseEntity<List<BranchEntity>> getBranches() {
        return ResponseEntity.ok(branchService.getBranches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchEntity> getBranchById(@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @PostMapping
    public ResponseEntity<BranchEntity> createBranch(@RequestBody @Valid SaveBranchRequest request) {
        return new ResponseEntity<>(branchService.saveBranch(request, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchEntity> updateBranch(@RequestBody @Valid SaveBranchRequest request, @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(branchService.saveBranch(request, id));
    }
}
