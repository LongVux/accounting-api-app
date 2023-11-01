package com.outwork.accountingapiapp.utils;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BillCodeHandler {
    private static final String BILL_CODE_REGEX = "^[A-Z0-9]+-\\d{6}-\\d+$";
    private static final String ERROR_MSG_INVALID_BILL_CDOE = "Mã Bill không hợp lệ";
    public static String generateBillCode(@NotNull String posCode, String latestBillCode) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DataFormat.DATE_FORMAT_ddMMyy);
        String dateString = date.format(formatter);

        int orderNumber = getOrderNumber(latestBillCode) + 1;

        return String.join(DataFormat.DEFAULT_SEPARATOR, posCode, dateString, Integer.toString(orderNumber));
    }

    public static void validateBillCode(String code) {
        if (ObjectUtils.isEmpty(code) || !code.matches(BILL_CODE_REGEX)) {
            throw new InvalidDataException(ERROR_MSG_INVALID_BILL_CDOE);
        }
    }

    public static int getOrderNumber(String code) {
        if (ObjectUtils.isEmpty(code)) {
            return 0;
        } else {
            validateBillCode(code);

            String[] parts = code.split(DataFormat.DEFAULT_SEPARATOR);
            String number = parts[parts.length - 1];

            return Integer.parseInt(number);
        }
    }
}
