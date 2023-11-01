package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.constants.PosStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SavePosRequest {

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ENTITY_CODE_MAX_LENGTH)
    private String code;

    @Size(min = DataConstraint.SHORT_STRING_MIN_LENGTH, max = DataConstraint.ID_STRING_MAX_LENGTH)
    private String name;

    @NotNull
    private PosStatusEnum posStatus;

    @NotBlank
    private String address;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String bank;

    private int maxBillAmount;

    private List<SupportedCardType> supportedCardTypes;
}
