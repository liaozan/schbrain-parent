package com.schbrain.common.constants;

import java.time.format.DateTimeFormatter;

import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author liaozan
 * @since 2021/10/15
 */
@SuppressWarnings("unused")
public class DateTimeFormatters {

    public static final String YEAR_MONTH_PATTERN = "yyyy-MM";
    public static final String MONTH_DATE_PATTERN = "MM-dd";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_PATTERN = "HH:mm:ss";

    public static final DateTimeFormatter YEAR_MONTH = ofPattern(YEAR_MONTH_PATTERN).withZone(systemDefault());
    public static final DateTimeFormatter MONTH_DATE = ofPattern(MONTH_DATE_PATTERN).withZone(systemDefault());
    public static final DateTimeFormatter DATE = ofPattern(DATE_PATTERN).withZone(systemDefault());
    public static final DateTimeFormatter DATE_TIME = ofPattern(DATE_TIME_PATTERN).withZone(systemDefault());
    public static final DateTimeFormatter TIME = ofPattern(TIME_PATTERN).withZone(systemDefault());

    public static final String YEAR_MONTH_WITH_SLASH_PATTERN = "yyyy/MM";
    public static final String DATE_WITH_SLASH_PATTERN = "yyyy/MM/dd";

    public static final DateTimeFormatter YEAR_MONTH_WITH_SLASH = ofPattern(YEAR_MONTH_WITH_SLASH_PATTERN).withZone(systemDefault());
    public static final DateTimeFormatter DATE_WITH_SLASH = ofPattern(DATE_WITH_SLASH_PATTERN).withZone(systemDefault());

}