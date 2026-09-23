package com.stockms.common.util;

import com.stockms.common.constants.AppConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.Timestamp;

/**
 * Centralized date/time formatting and parsing helpers used across every
 * module so that persistence (yyyy-MM-dd style) and on-screen display
 * (dd-MMM-yyyy style) formats stay consistent throughout the application.
 */
public final class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(AppConstants.DATE_TIME_FORMAT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern(AppConstants.DISPLAY_DATE_FORMAT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(AppConstants.DISPLAY_DATE_TIME_FORMAT);

    private DateUtil() {
        // Static utility class; no instances.
    }

    /** Returns the current timestamp, used for created_at / updated_at columns. */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /** Converts a {@link LocalDateTime} to a JDBC {@link Timestamp} for PreparedStatement binding. */
    public static Timestamp toSqlTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Timestamp.valueOf(dateTime);
    }

    /** Converts a JDBC {@link Timestamp} (as read from a ResultSet) into a {@link LocalDateTime}. */
    public static LocalDateTime fromSqlTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    /** Converts a {@link LocalDate} to a JDBC {@link java.sql.Date}. */
    public static java.sql.Date toSqlDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return java.sql.Date.valueOf(date);
    }

    /** Converts a JDBC {@link java.sql.Date} to a {@link LocalDate}. */
    public static LocalDate fromSqlDate(java.sql.Date date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate();
    }

    /** Formats a {@link LocalDateTime} using the storage format (yyyy-MM-dd HH:mm:ss). */
    public static String formatForStorage(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /** Formats a {@link LocalDate} using the storage format (yyyy-MM-dd). */
    public static String formatDateForStorage(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    /** Formats a {@link LocalDateTime} for friendly on-screen display (dd-MMM-yyyy hh:mm a). */
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DISPLAY_DATE_TIME_FORMATTER);
    }

    /** Formats a {@link LocalDate} for friendly on-screen display (dd-MMM-yyyy). */
    public static String formatDateForDisplay(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Parses a user-entered date string (expected format dd-MMM-yyyy or yyyy-MM-dd)
     * into a {@link LocalDate}, trying the display format first and falling back
     * to the storage format.
     *
     * @throws java.time.format.DateTimeParseException if neither format matches.
     */
    public static LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new DateTimeParseException("Date text is empty", text == null ? "" : text, 0);
        }
        String trimmed = text.trim();
        try {
            return LocalDate.parse(trimmed, DISPLAY_DATE_FORMATTER);
        } catch (DateTimeParseException displayFormatFailure) {
            return LocalDate.parse(trimmed, DATE_FORMATTER);
        }
    }

    /** Returns true if the given text can be parsed as a valid date by {@link #parseDate(String)}. */
    public static boolean isValidDateString(String text) {
        try {
            parseDate(text);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
