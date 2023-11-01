package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String password;
}
