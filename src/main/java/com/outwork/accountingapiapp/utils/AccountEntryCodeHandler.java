package com.outwork.accountingapiapp.utils;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.exceptions.InvalidDataException;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AccountEntryCodeHandler {
    private static final String RECEIPT_CODE_REGEX = "^[A-Z0-9]+-[A-Z]+-\\d{6}-\\d+$";

    private static final String ERROR_MSG_INVALID_ENTRY_CODE = "Mã bút toán không hợp lệ";

    public static String generateAccountEntryCode(@NotNull String branchCode, @NotNull TransactionTypeEnum transactionType, String latestEntryCode) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DataFormat.DATE_FORMAT_ddMMyy);
        String dateString = date.format(formatter);

        // Get the order number from the latest code
        int orderNumber = getOrderNumber(latestEntryCode) + 1;

        return String.join(
                DataFormat.DEFAULT_SEPARATOR,
                branchCode,
                MapBuilder.buildTransactionTypeString()
                        .get(transactionType),
                dateString,
                Integer.toString(orderNumber)
        );
    }

    // A method to check if a given string has the order string format
    public static void validateEntryCode(String code) {
        if (ObjectUtils.isEmpty(code) || !code.matches(RECEIPT_CODE_REGEX)) {
            throw new InvalidDataException(ERROR_MSG_INVALID_ENTRY_CODE);
        }
    }

    // A method to extract the order number from a given order string
    public static int getOrderNumber(String code) {
        if (ObjectUtils.isEmpty(code)) {
            return 0;
        } else {
            validateEntryCode(code);

            // Split the string by the dash and get the last component
            String[] parts = code.split(DataFormat.DEFAULT_SEPARATOR);
            String number = parts[parts.length - 1];

            // Parse and return the order number as an integer
            return Integer.parseInt(number);
        }
    }

}
