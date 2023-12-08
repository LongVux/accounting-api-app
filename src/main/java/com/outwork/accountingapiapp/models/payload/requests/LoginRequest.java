package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "{msg.err.string.blank}")
    private String code;

    @NotBlank(message = "{msg.err.string.blank}")
    private String password;
}
