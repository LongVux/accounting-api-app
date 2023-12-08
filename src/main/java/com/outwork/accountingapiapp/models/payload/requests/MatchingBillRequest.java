package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MatchingBillRequest {
    @NotBlank(message = "{msg.err.string.blank}")
    private List<UUID> billIds;

    @NotBlank(message = "{msg.err.string.blank}")
    private String explanation;

    @NotBlank(message = "{msg.err.string.blank}")
    private String imageId;
}
