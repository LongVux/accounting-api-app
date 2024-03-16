package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.PosEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetPosTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.SavePosRequest;
import com.outwork.accountingapiapp.models.payload.responses.PosTableItem;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedPos;
import com.outwork.accountingapiapp.services.PosService;
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
@RequestMapping("/api/poses")
public class PosController {

    @Autowired
    private PosService posService;

    @GetMapping
    public ResponseEntity<Page<PosTableItem>> getPosTableItems (@ModelAttribute @Valid GetPosTableItemRequest request) {
        return ResponseEntity.ok(posService.getPosTableItems(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PosEntity> getPosById (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(posService.getPosById(id));
    }

    @GetMapping("/searchByCode")
    public ResponseEntity<List<SuggestedPos>> searchPosByCode(@RequestParam @Size(min = 2) String searchKey, @RequestParam UUID branchId) {
        return ResponseEntity.ok(posService.searchPosByCodeAndBranch(searchKey, branchId));
    }

    @PostMapping
    public ResponseEntity<PosEntity> createPos (@RequestBody @Valid SavePosRequest request) {
        return new ResponseEntity<>(posService.savePos(request, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PosEntity> updatePos (@RequestBody @Valid SavePosRequest request, @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(posService.savePos(request, id));
    }

    @DeleteMapping("/{id}")
    public void deletePos (@PathVariable UUID id) {
        posService.deletePos(id);
    }
}
