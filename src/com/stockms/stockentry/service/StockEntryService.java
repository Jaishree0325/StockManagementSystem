package com.stockms.stockentry.service;

import com.stockms.common.db.DBConnection;
import com.stockms.stockentry.dao.StockEntryDAO;
import com.stockms.stockentry.model.StockEntry;
import java.sql.Connection;
import java.sql.SQLException;

public class StockEntryService {
    
    private final StockEntryDAO stockEntryDAO;

    public StockEntryService() {
        this.stockEntryDAO = new StockEntryDAO();
    }

    public boolean processStockEntry(StockEntry entry) throws Exception {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // Start transaction
            conn.setAutoCommit(false);
            
            stockEntryDAO.insertStockEntryAndUpdateProduct(conn, entry);
            
            // Commit transaction if successful
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new Exception("Error rolling back transaction: " + ex.getMessage());
                }
            }
            throw new Exception("Database error during stock entry: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}