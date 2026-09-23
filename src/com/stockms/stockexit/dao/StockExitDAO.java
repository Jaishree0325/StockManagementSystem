package com.stockms.stockexit.dao;

import com.stockms.stockexit.model.StockExit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockExitDAO {

    public int getCurrentStock(Connection conn, int productId) throws SQLException {
        String query = "SELECT quantity FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        }
        return 0; // Return 0 if product not found
    }

    public void insertStockExitAndUpdateProduct(Connection conn, StockExit exit) throws SQLException {
        String insertLogSQL = "INSERT INTO stock_exits (product_id, quantity, exit_date, reason) VALUES (?, ?, ?, ?)";
        String updateProductSQL = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";

        try (PreparedStatement insertStmt = conn.prepareStatement(insertLogSQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateProductSQL)) {
            
            // 1. Insert the exit log
            insertStmt.setInt(1, exit.getProductId());
            insertStmt.setInt(2, exit.getQuantity());
            insertStmt.setDate(3, new java.sql.Date(exit.getExitDate().getTime()));
            insertStmt.setString(4, exit.getReason());
            insertStmt.executeUpdate();

            // 2. Decrement the product stock
            updateStmt.setInt(1, exit.getQuantity());
            updateStmt.setInt(2, exit.getProductId());
            updateStmt.executeUpdate();
        }
    }
}