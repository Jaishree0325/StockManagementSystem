package com.stockms.common.constants;

/**
 * Central location for application-wide constant values: database
 * connection settings, date/time formats, and business-rule defaults.
 *
 * Values here are intentionally simple hard-coded defaults suitable for a
 * NetBeans desktop project. In a deployed environment these would typically
 * be externalized to a properties file, but keeping them as constants keeps
 * the project runnable out-of-the-box with zero extra configuration.
 */
public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation; this class only exposes static constants.
    }

    // ---------------------------------------------------------------
    // Database connection settings
    // ---------------------------------------------------------------
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_HOST = "localhost";
    public static final int DB_PORT = 3306;
    public static final String DB_NAME = "stock_management_db";
    public static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "12345";
    public static final int DB_CONNECTION_TIMEOUT_SECONDS = 10;

    // ---------------------------------------------------------------
    // Application metadata
    // ---------------------------------------------------------------
    public static final String APP_TITLE = "Stock Management System";
    public static final String APP_VERSION = "1.0.0";

    // ---------------------------------------------------------------
    // Date / time formats
    // ---------------------------------------------------------------
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "dd-MMM-yyyy";
    public static final String DISPLAY_DATE_TIME_FORMAT = "dd-MMM-yyyy hh:mm a";

    // ---------------------------------------------------------------
    // Business rule defaults
    // ---------------------------------------------------------------
    public static final int DEFAULT_REORDER_LEVEL = 10;
    public static final int MIN_STOCK_QUANTITY = 0;
    public static final String DEFAULT_UNIT = "pcs";

    // ---------------------------------------------------------------
    // Validation limits
    // ---------------------------------------------------------------
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_SKU_LENGTH = 30;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    public static final int MAX_PHONE_LENGTH = 20;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MAX_ADDRESS_LENGTH = 250;
}
