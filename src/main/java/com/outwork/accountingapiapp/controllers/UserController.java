package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.entity.UserEntity;
import com.outwork.accountingapiapp.models.payload.requests.GetUserTableItemRequest;
import com.outwork.accountingapiapp.models.payload.requests.UpdateUserRequest;
import com.outwork.accountingapiapp.models.payload.responses.UserTableItem;
import com.outwork.accountingapiapp.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserTableItem>> getUserTableItems (@ModelAttribute @Valid GetUserTableItemRequest request) {
        return ResponseEntity.ok(userService.getUserTableItems(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById (@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(userService.getUserEntityById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUserById (@PathVariable @NotNull UUID id, @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request, id));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById (@PathVariable @NotNull UUID id) {
        userService.deleteUser(id);
    }
}
