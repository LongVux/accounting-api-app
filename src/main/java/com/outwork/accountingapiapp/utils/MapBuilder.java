package com.outwork.accountingapiapp.utils;

import com.outwork.accountingapiapp.constants.*;
import com.outwork.accountingapiapp.models.entity.*;

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

    public static Map<PosSortingEnum, String> buildPosTableItemSorter () {
        Map<PosSortingEnum, String> sorterMap = new HashMap<>();

        sorterMap.put(PosSortingEnum.code, PosEntity.FIELD_CODE);
        sorterMap.put(PosSortingEnum.name, PosEntity.FIELD_NAME);
        sorterMap.put(PosSortingEnum.accountNumber, PosEntity.FIELD_ACCOUNT_NUMBER);
        sorterMap.put(PosSortingEnum.bank, PosEntity.FIELD_BANK);
        sorterMap.put(PosSortingEnum.maxBillAmount, PosEntity.FIELD_MAX_BILL_AMOUNT);

        return sorterMap;
    }

    public static Map<UserSortingEnum, String> buildUserTableItemSorter () {
        Map<UserSortingEnum, String> sorterMap = new HashMap<>();

        sorterMap.put(UserSortingEnum.name, UserEntity.FIELD_NAME);
        sorterMap.put(UserSortingEnum.code, UserEntity.FIELD_CODE);
        sorterMap.put(UserSortingEnum.email, UserEntity.FIELD_EMAIL);
        sorterMap.put(UserSortingEnum.phoneNumber, UserEntity.FIELD_PHONE_NUMBER);
        sorterMap.put(UserSortingEnum.accountBalance, UserEntity.FIELD_ACCOUNT_BALANCE);

        return sorterMap;
    }

    public static Map<CustomerSortingEnum, String> buildCustomerTableItemSorter () {
        Map<CustomerSortingEnum, String> sorterMap = new HashMap<>();

        sorterMap.put(CustomerSortingEnum.name, CustomerEntity.FIELD_NAME);
        sorterMap.put(CustomerSortingEnum.address, CustomerEntity.FIELD_ADDRESS);

        return sorterMap;
    }

    public static Map<CustomerCardSortingEnum, String> buildCustomerCardTableItemSorter () {
        Map<CustomerCardSortingEnum, String> sorterMap = new HashMap<>();

        sorterMap.put(CustomerCardSortingEnum.name, CustomerCardEntity.FIELD_NAME);
        sorterMap.put(CustomerCardSortingEnum.cardType, String.join(DataFormat.DOT_SEPARATOR, CustomerCardEntity.FIELD_CARD_TYPE, CardTypeEntity.FIELD_NAME));
        sorterMap.put(CustomerCardSortingEnum.bank, CustomerCardEntity.FIELD_BANK);
        sorterMap.put(CustomerCardSortingEnum.customerName, String.join(DataFormat.DOT_SEPARATOR, CustomerCardEntity.FIELD_CUSTOMER, CustomerEntity.FIELD_NAME));
        sorterMap.put(CustomerCardSortingEnum.paymentLimit, CustomerCardEntity.FIELD_PAYMENT_LIMIT);
        sorterMap.put(CustomerCardSortingEnum.paymentDueDate, CustomerCardEntity.FIELD_PAYMENT_DUE_DATE);
        sorterMap.put(CustomerCardSortingEnum.createdDate, CustomerCardEntity.FIELD_CREATED_DATE);
        sorterMap.put(CustomerCardSortingEnum.accountNumber, CustomerCardEntity.FIELD_ACCOUNT_NUMBER);

        return sorterMap;
    }

    public static Map<BranchAccountEntrySortingEnum, String> buildBranchAccountTableItemSorter () {
        Map<BranchAccountEntrySortingEnum, String> sorterMap = new HashMap<>();

        sorterMap.put(BranchAccountEntrySortingEnum.createdDate, BranchAccountEntryEntity.FIELD_CREATED_DATE);
        sorterMap.put(BranchAccountEntrySortingEnum.entryCode, BranchAccountEntryEntity.FIELD_ENTRY_CODE);
        sorterMap.put(BranchAccountEntrySortingEnum.entryType, BranchAccountEntryEntity.FIELD_ENTRY_TYPE);
        sorterMap.put(BranchAccountEntrySortingEnum.branchCode, String.join(DataFormat.DOT_SEPARATOR, BranchAccountEntryEntity.FIELD_BRANCH, BranchEntity.FIELD_CODE));
        sorterMap.put(BranchAccountEntrySortingEnum.lastModifiedBy, BranchAccountEntryEntity.FIELD_LAST_MODIFIED_BY);

        return sorterMap;
    }

    public static Map<GeneralAccountEntrySortingEnum, String> buildGeneralAccountTableItemSorter () {
        Map<GeneralAccountEntrySortingEnum, String> sorterMap = new HashMap<>();

        sorterMap.put(GeneralAccountEntrySortingEnum.createdDate, GeneralAccountEntryEntity.FIELD_CREATED_DATE);
        sorterMap.put(GeneralAccountEntrySortingEnum.entryCode, GeneralAccountEntryEntity.FIELD_ENTRY_CODE);
        sorterMap.put(GeneralAccountEntrySortingEnum.entryType, GeneralAccountEntryEntity.FIELD_ENTRY_TYPE);

        return sorterMap;
    }

    public static Map<BillSortingEnum, String> buildBillTableItemSorter () {
        Map<BillSortingEnum, String> sorterMap = new HashMap<>();

        sorterMap.put(BillSortingEnum.createdDate, BillEntity.FIELD_CREATED_DATE);
        sorterMap.put(BillSortingEnum.code, BillEntity.FIELD_CODE);
        sorterMap.put(BillSortingEnum.posCode, String.join(DataFormat.DOT_SEPARATOR, BillEntity.FIELD_POS, PosEntity.FIELD_CODE));
        sorterMap.put(BillSortingEnum.moneyAmount, BillEntity.FIELD_MONEY_AMOUNT);
        sorterMap.put(BillSortingEnum.fee, BillEntity.FIELD_FEE);
        sorterMap.put(BillSortingEnum.posFeeStamp, BillEntity.FIELD_POS_FEE_STAMP);
        sorterMap.put(BillSortingEnum.returnFromBank, BillEntity.FIELD_RETURN_FROM_BANK);
        sorterMap.put(BillSortingEnum.returnedTime, BillEntity.FIELD_RETURNED_TIME);
        sorterMap.put(BillSortingEnum.receiptCode, String.join(DataFormat.DOT_SEPARATOR, BillEntity.FIELD_RECEIPT, ReceiptEntity.FIELD_CODE));

        return sorterMap;
    }

    public static Map<TransactionTypeEnum, String> buildTransactionTypeString () {
        Map<TransactionTypeEnum, String> map = new HashMap<>();

        map.put(TransactionTypeEnum.INTAKE, "THU");
        map.put(TransactionTypeEnum.PAYOUT, "CHI");
        map.put(TransactionTypeEnum.LOAN, "NO");
        map.put(TransactionTypeEnum.REPAYMENT, "THUNO");

        return map;
    }
}
