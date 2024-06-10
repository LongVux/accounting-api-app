package com.outwork.accountingapiapp.models.payload.responses;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
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
    @CsvIgnore
    private UUID id;

    @CsvBindByName(column = "A. Ngay tao")
    @CsvDate("dd.MM.yyyy hh:mm")
    private Date createdDate;

    @CsvBindByName(column = "B. Nguoi tao")
    private String createdBy;

    @CsvBindByName(column = "C. Ma hoa don")
    private String receiptCode;

    @CsvBindByName(column = "D. Ma POS")
    private String posCode;

    @CsvBindByName(column = "E. Ma bill")
    private String code;

    @CsvBindByName(column = "F. So tien")
    private double moneyAmount;

    @CsvBindByName(column = "G. So phi bill")
    private double fee;

    @CsvBindByName(column = "H. Phan tram phi POS")
    private double posFeeStamp;

    @CsvBindByName(column = "I. Tien ve du tinh")
    private double estimateReturnFromBank;

    @CsvBindByName(column = "J. Tien ve thuc te")
    private double returnFromBank;

    @CsvBindByName(column = "K. Thoi diem ve tien")
    @CsvDate("dd.MM.yyyy hh:mm")
    private Date returnedTime;

    @CsvBindByName(column = "L. Ghi chu")
    private String note;

    @CsvIgnore
    private long timeStampSeq;

    @CsvIgnore
    private RecordStatusEnum recordStatusEnum;

    @CsvIgnore
    private UUID posId;

    public BillTableItem (BillEntity bill) {
        this.createdBy = bill.getCreatedBy();
        this.createdDate = bill.getCreatedDate();
        this.recordStatusEnum = bill.getRecordStatusEnum();
        this.id = bill.getId();
        this.code = bill.getCode();
        this.timeStampSeq = bill.getTimeStampSeq();
        this.moneyAmount = bill.getMoneyAmount();
        this.fee = bill.getFee();
        this.posFeeStamp = bill.getPosFeeStamp();
        this.estimateReturnFromBank = bill.getEstimatedReturnFromBank();
        this.returnFromBank = bill.getReturnFromBank();
        this.returnedTime = bill.getReturnedTime();
        this.posCode = bill.getPos().getCode();
        this.posId = bill.getPos().getId();
        this.receiptCode = bill.getReceipt().getCode();
        this.note = bill.getNote();
    }
}