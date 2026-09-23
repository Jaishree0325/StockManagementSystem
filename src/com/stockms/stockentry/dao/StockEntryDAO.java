package com.stockms.stockentry.dao;

import com.stockms.stockentry.model.StockEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StockEntryDAO {

    public void insertStockEntryAndUpdateProduct(Connection conn, StockEntry entry) throws SQLException {
        String insertLogSQL = "INSERT INTO stock_entries (product_id, supplier_id, quantity, entry_date, remarks) VALUES (?, ?, ?, ?, ?)";
        String updateProductSQL = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";

        try (PreparedStatement insertStmt = conn.prepareStatement(insertLogSQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateProductSQL)) {
            
            // 1. Insert the entry log
            insertStmt.setInt(1, entry.getProductId());
            insertStmt.setInt(2, entry.getSupplierId());
            insertStmt.setInt(3, entry.getQuantity());
            insertStmt.setDate(4, new java.sql.Date(entry.getEntryDate().getTime()));
            insertStmt.setString(5, entry.getRemarks());
            insertStmt.executeUpdate();

            // 2. Increment the product stock
            updateStmt.setInt(1, entry.getQuantity());
            updateStmt.setInt(2, entry.getProductId());
            updateStmt.executeUpdate();
        }
    }
}