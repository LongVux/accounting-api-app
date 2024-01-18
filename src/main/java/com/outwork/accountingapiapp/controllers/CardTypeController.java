package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import com.outwork.accountingapiapp.models.entity.CardTypeEntity;
import com.outwork.accountingapiapp.models.payload.requests.SaveCardTypeRequest;
import com.outwork.accountingapiapp.services.CardTypeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cardTypes")
public class CardTypeController {
    public static final String ERROR_MSG_CANNOT_DELETE = "Dữ liệu này đã được sử dụng trong hệ thống, không thể xóa!";

    @Autowired
    private CardTypeService cardTypeService;

    @GetMapping
    public ResponseEntity<List<CardTypeEntity>> getCardTypes () {
        return ResponseEntity.ok(cardTypeService.getCardTypes());
    }

    @PostMapping
    public ResponseEntity<CardTypeEntity> createCardType (@RequestBody @Valid SaveCardTypeRequest request) {
        return new ResponseEntity<>(cardTypeService.saveCardType(request, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardTypeEntity> updateCardType (@RequestBody @Valid SaveCardTypeRequest request, @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(cardTypeService.saveCardType(request, id));
    }

    @DeleteMapping("/{id}")
    public void deleteCardType (@PathVariable @NotNull UUID id) {
        try {
            cardTypeService.deleteCardTypeById(id);
        } catch (Exception e) {
            throw new InvalidDataException(ERROR_MSG_CANNOT_DELETE);
        }
    }
}
