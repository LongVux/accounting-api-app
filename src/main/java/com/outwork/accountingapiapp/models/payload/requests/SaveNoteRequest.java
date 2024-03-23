package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class SaveNoteRequest {
    @NotNull(message = "{msg.err.string.blank}")
    private UUID id;

    @Size(
            max = 255,
            message = "{msg.err.string.range}"
    )
    private String note;
}
