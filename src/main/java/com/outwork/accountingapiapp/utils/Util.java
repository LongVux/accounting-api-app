package com.outwork.accountingapiapp.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Util {
    public String getSimpleMessage(String title, String content) {
        if (StringUtils.isEmpty(title) && StringUtils.isEmpty(content)) {
            return "";
        }

        if (StringUtils.isEmpty(title)) {
            return content;
        }

        if (StringUtils.isEmpty(content)) {
            return title;
        }

        return title + ": " + content;
    }
}
