package com.outwork.accountingapiapp.controllers;

import com.outwork.accountingapiapp.models.payload.requests.LoginRequest;
import com.outwork.accountingapiapp.models.payload.requests.SignupRequest;
import com.outwork.accountingapiapp.models.payload.responses.UserDetail;
import com.outwork.accountingapiapp.services.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @SecurityRequirements
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody @Valid SignupRequest signupRequest) {
        return new ResponseEntity<>(authService.registerUser(signupRequest), HttpStatus.CREATED);
    }

    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<UserDetail> login (@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
