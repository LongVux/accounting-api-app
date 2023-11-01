package com.outwork.accountingapiapp.models.payload.requests;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SaveReceiptRepaymentEntryRequest extends SaveReceiptEntryRequest {
    private int repaidAmount;
}
