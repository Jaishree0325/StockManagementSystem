package com.stockms.common.exception;

/**
 * Application-wide checked exception used across every module (Product,
 * Supplier, Stock Entry, Stock Exit, Reports, Alerts) to signal business-rule
 * violations, validation failures, or wrapped low-level failures such as
 * {@link java.sql.SQLException}s raised from the DAO layer.
 *
 * Using a single checked exception type at the service/DAO boundary keeps
 * calling code (Swing UI panels) simple: callers only ever need to catch one
 * exception type and can rely on {@link #getErrorCode()} to distinguish
 * between failure categories if they need to react differently.
 */
public class StockManagementException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Machine-readable error code, one of the ErrorCode constants below. */
    private final String errorCode;

    public static final String ERR_VALIDATION = "VALIDATION_ERROR";
    public static final String ERR_DATABASE = "DATABASE_ERROR";
    public static final String ERR_NOT_FOUND = "NOT_FOUND";
    public static final String ERR_DUPLICATE = "DUPLICATE_ENTRY";
    public static final String ERR_BUSINESS_RULE = "BUSINESS_RULE_VIOLATION";
    public static final String ERR_UNKNOWN = "UNKNOWN_ERROR";

    public StockManagementException(String message) {
        super(message);
        this.errorCode = ERR_UNKNOWN;
    }

    public StockManagementException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public StockManagementException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ERR_UNKNOWN;
    }

    public StockManagementException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "StockManagementException{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
