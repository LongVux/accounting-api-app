package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.constants.PosStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SavePosRequest {

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ENTITY_CODE_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    @Pattern(
            regexp = DataConstraint.CAPITAL_CHAR_AND_DIGIT_ONLY_REGEX,
            message = "{msg.err.string.regexp}"
    )
    private String code;

    @Size(
            min = DataConstraint.SHORT_STRING_MIN_LENGTH,
            max = DataConstraint.ID_STRING_MAX_LENGTH,
            message = "{msg.err.string.range}"
    )
    private String name;

    @NotNull(message = "{msg.err.string.blank}")
    private PosStatusEnum posStatus;

    @NotBlank(message = "{msg.err.string.blank}")
    private String address;

    @NotBlank(message = "{msg.err.string.blank}")
    private String accountNumber;

    @NotBlank(message = "{msg.err.string.blank}")
    private String bank;

    private int maxBillAmount;

    private List<@Valid SupportedCardType> supportedCardTypes;
}
