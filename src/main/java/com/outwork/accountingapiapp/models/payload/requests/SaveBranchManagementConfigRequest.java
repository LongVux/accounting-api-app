package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SaveBranchManagementConfigRequest {
    @NotNull(message = "{msg.err.string.blank}")
    private UUID branchId;

    @NotNull(message = "{msg.err.string.blank}")
    private Integer orderId;
}
