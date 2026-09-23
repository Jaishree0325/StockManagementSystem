package com.stockms.common.util;

import com.stockms.common.constants.AppConstants;

import java.util.regex.Pattern;

/**
 * Reusable field-level validation helpers shared by every module's Service
 * layer (Product, Supplier, Stock Entry, Stock Exit). Keeping validation
 * logic here avoids duplicating regex/range checks across services.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[+]?[0-9()\\-\\s]{7,20}$");

    private static final Pattern SKU_PATTERN =
            Pattern.compile("^[A-Za-z0-9][A-Za-z0-9_-]{2,29}$");

    private ValidationUtil() {
        // Static utility class; no instances.
    }

    /** Returns true if the string is null, empty, or made up only of whitespace. */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** Returns true if the string has content beyond whitespace. */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /** Validates that a required text field is present and within a maximum length. */
    public static boolean isValidLength(String value, int maxLength) {
        if (value == null) {
            return false;
        }
        return value.trim().length() > 0 && value.trim().length() <= maxLength;
    }

    /** Validates an e-mail address format. Empty strings are considered invalid for required fields. */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches()
                && email.trim().length() <= AppConstants.MAX_EMAIL_LENGTH;
    }

    /** Validates a phone number: digits, spaces, parentheses, dashes, and an optional leading +. */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates a SKU (Stock Keeping Unit) code: alphanumeric, may contain
     * hyphens/underscores after the first character, 3-30 characters long.
     */
    public static boolean isValidSku(String sku) {
        if (isEmpty(sku)) {
            return false;
        }
        return SKU_PATTERN.matcher(sku.trim()).matches();
    }

    /** Returns true if the text parses as an integer. */
    public static boolean isInteger(String value) {
        if (isEmpty(value)) {
            return false;
        }
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /** Returns true if the text parses as a non-negative integer. */
    public static boolean isNonNegativeInteger(String value) {
        if (!isInteger(value)) {
            return false;
        }
        return Integer.parseInt(value.trim()) >= 0;
    }

    /** Returns true if the text parses as a strictly positive integer (> 0). */
    public static boolean isPositiveInteger(String value) {
        if (!isInteger(value)) {
            return false;
        }
        return Integer.parseInt(value.trim()) > 0;
    }

    /** Returns true if the text parses as a decimal number. */
    public static boolean isDecimal(String value) {
        if (isEmpty(value)) {
            return false;
        }
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /** Returns true if the text parses as a non-negative decimal number (>= 0.0). */
    public static boolean isNonNegativeDecimal(String value) {
        if (!isDecimal(value)) {
            return false;
        }
        return Double.parseDouble(value.trim()) >= 0.0;
    }

    /** Returns true if the text parses as a strictly positive decimal number (> 0.0). */
    public static boolean isPositiveDecimal(String value) {
        if (!isDecimal(value)) {
            return false;
        }
        return Double.parseDouble(value.trim()) > 0.0;
    }
}
