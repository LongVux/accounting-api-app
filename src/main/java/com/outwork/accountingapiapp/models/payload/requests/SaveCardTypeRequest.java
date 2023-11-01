package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveCardTypeRequest {

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ID_STRING_MAX_LENGTH)
    private String name;
}
