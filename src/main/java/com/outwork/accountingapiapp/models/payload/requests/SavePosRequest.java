package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.constants.PosStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

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

    @Min(
            value = 1000,
            message = "{msg.err.double.min}"
    )
    private int maxBillAmount;

    private List<@Valid SupportedCardType> supportedCardTypes;

    @Size(
            max = 255,
            message = "{msg.err.string.range}"
    )
    private String note;

    @Nullable
    private List<UUID> branchIds;
}
