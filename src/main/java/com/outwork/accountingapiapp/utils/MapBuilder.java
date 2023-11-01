package com.outwork.accountingapiapp.utils;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.constants.ReceiptSortingEnum;
import com.outwork.accountingapiapp.models.entity.CustomerCardEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.entity.UserEntity;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {
    public static Map<ReceiptSortingEnum, String> buildReceiptTableItemSorter () {
        Map<ReceiptSortingEnum, String> sorterMap = new HashMap<>();

        sorterMap.put(ReceiptSortingEnum.code, ReceiptEntity.FIELD_CODE);
        sorterMap.put(ReceiptSortingEnum.employeeCode, String.join(DataFormat.DOT_SEPARATOR, ReceiptEntity.FIELD_EMPLOYEE, UserEntity.FIELD_CODE));
        sorterMap.put(ReceiptSortingEnum.createdDate, ReceiptEntity.FIELD_CREATED_DATE);
        sorterMap.put(ReceiptSortingEnum.cardName, String.join(DataFormat.DOT_SEPARATOR, ReceiptEntity.FIELD_CUSTOMER_CARD, CustomerCardEntity.FIELD_NAME));
        sorterMap.put(ReceiptSortingEnum.intake, ReceiptEntity.FIELD_INTAKE);
        sorterMap.put(ReceiptSortingEnum.payout, ReceiptEntity.FIELD_PAYOUT);
        sorterMap.put(ReceiptSortingEnum.loan, ReceiptEntity.FIELD_LOAN);
        sorterMap.put(ReceiptSortingEnum.repayment, ReceiptEntity.FIELD_REPAYMENT);
        sorterMap.put(ReceiptSortingEnum.transactionTotal, ReceiptEntity.FIELD_TRANSACTION_TOTAL);
        sorterMap.put(ReceiptSortingEnum.calculatedProfit, ReceiptEntity.FIELD_CALCULATED_PROFIT);
        sorterMap.put(ReceiptSortingEnum.estimatedProfit, ReceiptEntity.FIELD_ESTIMATED_PROFIT);
        sorterMap.put(ReceiptSortingEnum.receiptStatus, ReceiptEntity.FIELD_RECEIPT_STATUS);

        return sorterMap;
    }
}
