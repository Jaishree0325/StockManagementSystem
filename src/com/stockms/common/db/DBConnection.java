package com.stockms.common.db;

import com.stockms.common.constants.AppConstants;
import com.stockms.common.exception.StockManagementException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides JDBC {@link Connection} instances to MySQL for every DAO class in
 * the application (Product, Supplier, Stock Entry, Stock Exit, Reports,
 * Alerts). The MySQL JDBC driver class is loaded once in a static
 * initializer block so callers do not need to worry about
 * {@code Class.forName} plumbing.
 *
 * Each call to {@link #getConnection()} returns a brand-new {@link Connection}.
 * DAO methods are expected to open the connection, do their work inside a
 * try-with-resources block, and let it close automatically - this keeps the
 * application simple and avoids sharing a single connection across Swing
 * event-dispatch-thread actions.
 */
public final class DBConnection {

    private static volatile boolean driverLoaded = false;

    private DBConnection() {
        // Static utility class; no instances.
    }

    /**
     * Loads the MySQL Connector/J driver class exactly once. Safe to call
     * repeatedly; subsequent calls are no-ops.
     *
     * @throws StockManagementException if the JDBC driver class cannot be found
     *         on the classpath (e.g. mysql-connector-j.jar missing from lib/).
     */
    private static synchronized void loadDriverIfNeeded() throws StockManagementException {
        if (driverLoaded) {
            return;
        }
        try {
            Class.forName(AppConstants.DB_DRIVER);
            driverLoaded = true;
        } catch (ClassNotFoundException ex) {
            throw new StockManagementException(
                    "MySQL JDBC driver not found on classpath. Ensure mysql-connector-j.jar "
                            + "is present in the lib/ directory.",
                    StockManagementException.ERR_DATABASE,
                    ex);
        }
    }

    /**
     * Opens and returns a new JDBC {@link Connection} to the configured
     * MySQL database, using the URL/user/password defined in
     * {@link AppConstants}.
     *
     * @return an open, ready-to-use JDBC connection.
     * @throws StockManagementException if the driver cannot be loaded or the
     *         connection attempt fails (wrong credentials, database server
     *         unreachable, unknown schema, etc).
     */
    public static Connection getConnection() throws StockManagementException {
        loadDriverIfNeeded();
        try {
            Connection connection = DriverManager.getConnection(
                    AppConstants.DB_URL,
                    AppConstants.DB_USER,
                    AppConstants.DB_PASSWORD);
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException ex) {
            throw new StockManagementException(
                    "Unable to connect to the database at " + AppConstants.DB_URL
                            + ". Please verify the MySQL server is running and the "
                            + "connection settings in AppConstants are correct.",
                    StockManagementException.ERR_DATABASE,
                    ex);
        }
    }

    /**
     * Closes a {@link Connection} defensively, swallowing (but not hiding -
     * printing to stderr) any {@link SQLException} raised during close since
     * failing to close a connection is not something calling code can
     * meaningfully recover from.
     *
     * @param connection the connection to close; safe to pass null.
     */
    public static void closeQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.err.println("Warning: failed to close database connection cleanly: "
                    + ex.getMessage());
        }
    }

    /**
     * Tests whether a database connection can currently be established.
     * Used by the UI (e.g. a "Test Connection" menu action) to give the
     * administrator quick feedback about database availability.
     *
     * @return true if a connection was opened and closed successfully.
     */
    public static boolean testConnection() {
        Connection connection = null;
        try {
            connection = getConnection();
            return connection != null && !connection.isClosed();
        } catch (StockManagementException | SQLException ex) {
            return false;
        } finally {
            closeQuietly(connection);
        }
    }
}
