package com.krish.oibsip.reservation.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter DATE_DB_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
    private static final DateTimeFormatter DATE_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    
    private static final DateTimeFormatter DATETIME_DB_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME; // yyyy-MM-dd'T'HH:mm:ss...
    private static final DateTimeFormatter DATETIME_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    // Journey Date - LocalDate
    public static String formatLocalDateForDB(LocalDate date) {
        return date == null ? null : date.format(DATE_DB_FORMATTER);
    }

    public static LocalDate parseLocalDateFromDB(String dateStr) {
        return dateStr == null || dateStr.trim().isEmpty() ? null : LocalDate.parse(dateStr, DATE_DB_FORMATTER);
    }

    public static String formatLocalDateForDisplay(LocalDate date) {
        return date == null ? "" : date.format(DATE_DISPLAY_FORMATTER);
    }

    // Timestamp - LocalDateTime
    public static String formatLocalDateTimeForDB(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATETIME_DB_FORMATTER);
    }

    public static LocalDateTime parseLocalDateTimeFromDB(String dateTimeStr) {
        return dateTimeStr == null || dateTimeStr.trim().isEmpty() ? null : LocalDateTime.parse(dateTimeStr, DATETIME_DB_FORMATTER);
    }

    public static String formatLocalDateTimeForDisplay(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATETIME_DISPLAY_FORMATTER);
    }
}
