package com.stockms.stockexit.service;

import com.stockms.common.db.DBConnection;
import com.stockms.stockexit.dao.StockExitDAO;
import com.stockms.stockexit.model.StockExit;
import java.sql.Connection;
import java.sql.SQLException;

public class StockExitService {
    
    private final StockExitDAO stockExitDAO;

    public StockExitService() {
        this.stockExitDAO = new StockExitDAO();
    }

    public boolean processStockExit(StockExit exit) throws Exception {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Validate available stock
            int currentStock = stockExitDAO.getCurrentStock(conn, exit.getProductId());
            if (currentStock < exit.getQuantity()) {
                throw new Exception("Insufficient stock! Available quantity: " + currentStock);
            }
            
            // Process exit and update inventory
            stockExitDAO.insertStockExitAndUpdateProduct(conn, exit);
            
            // Commit transaction
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
            throw new Exception("Database error during stock exit: " + e.getMessage());
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