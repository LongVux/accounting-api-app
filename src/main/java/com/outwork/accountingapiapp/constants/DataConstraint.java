package com.outwork.accountingapiapp.constants;

public class DataConstraint {
    public static final int ENTITY_CODE_MAX_LENGTH = 10;
    public static final int SHORT_STRING_MIN_LENGTH = 2;
    public static final int SHORT_STRING_MAX_LENGTH = 50;
    public static final int ID_STRING_MAX_LENGTH = 25;
    public static final String DIGIT_ONLY_REGEX = "^\\d+$";
    public static final String CAPITAL_CHAR_AND_DIGIT_ONLY_REGEX = "^[A-Z0-9]*$";

    public static final String COMPANY_CARD_REGEX = "^CT_.*";
}
