package com.outwork.accountingapiapp.models.payload.responses;

import com.outwork.accountingapiapp.constants.RecordStatusEnum;
import com.outwork.accountingapiapp.models.entity.BillEntity;
import com.outwork.accountingapiapp.models.entity.PosCardFeeEntity;
import com.outwork.accountingapiapp.models.payload.requests.SupportedCardType;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.UUID;

@Data
public class BillTableItem {
    private String createdBy;
    private Date createdDate;
    private RecordStatusEnum recordStatusEnum;
    private UUID id;
    private String code;
    private long timeStampSeq;
    private double moneyAmount;
    private double fee;
    private double estimatedProfit;
    private double returnedProfit;
    private Date returnedTime;
    private String posCode;
    private double posFee;
    private String receiptCode;

    public BillTableItem (BillEntity bill) {
        this.createdBy = bill.getCreatedBy();
        this.createdDate = bill.getCreatedDate();
        this.recordStatusEnum = bill.getRecordStatusEnum();
        this.id = bill.getId();
        this.code = bill.getCode();
        this.timeStampSeq = bill.getTimeStampSeq();
        this.moneyAmount = bill.getMoneyAmount();
        this.fee = bill.getFee();
        this.estimatedProfit = bill.getEstimatedProfit();
        this.returnedProfit = bill.getReturnedProfit();
        this.returnedTime = bill.getReturnedTime();
        this.posCode = bill.getPos().getCode();
        this.receiptCode = bill.getReceipt().getCode();

        for (PosCardFeeEntity cardType : bill.getPos().getSupportedCardTypes()) {
            if (bill.getReceipt().getCustomerCard().getCardType().getId().equals(cardType.getId())) {
                this.posFee = cardType.getPosCardFee();
                break;
            }
        }


    }
}