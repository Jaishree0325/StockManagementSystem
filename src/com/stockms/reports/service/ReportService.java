package com.stockms.reports.service;

import com.stockms.common.db.DBConnection;
import com.stockms.reports.model.ReportData;
import com.stockms.reports.model.ReportData.StockMovement;
import com.stockms.reports.model.ReportData.TopSeller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    public ReportData generateFullReport() throws Exception {
        ReportData report = new ReportData();
        
        try (Connection conn = DBConnection.getConnection()) {
            
            // 1. Get Summary Stats (Assumes a 'price' column exists in products table for value)
            String summarySql = "SELECT COUNT(product_id) as total_items, " +
                                "SUM(quantity * COALESCE(price, 0)) as total_value, " +
                                "SUM(CASE WHEN quantity <= 10 THEN 1 ELSE 0 END) as low_stock " +
                                "FROM products";
            try (PreparedStatement ps = conn.prepareStatement(summarySql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setTotalProductsCount(rs.getInt("total_items"));
                    report.setTotalStockValue(rs.getDouble("total_value"));
                    report.setLowStockCount(rs.getInt("low_stock"));
                }
            }

            // 2. Get Top Sellers (Most exited products)
            String topSellersSql = "SELECT p.product_name, SUM(e.quantity) as total_sold " +
                                   "FROM stock_exits e " +
                                   "JOIN products p ON e.product_id = p.product_id " +
                                   "GROUP BY p.product_id, p.product_name " +
                                   "ORDER BY total_sold DESC LIMIT 5";
            List<TopSeller> topSellers = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(topSellersSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    topSellers.add(new TopSeller(rs.getString("product_name"), rs.getInt("total_sold")));
                }
            }
            report.setTopSellers(topSellers);

            // 3. Get Recent Stock Movements (UNION of entries and exits)
            String historySql = "SELECT 'INCOMING' as m_type, p.product_name, en.quantity, en.entry_date as action_date " +
                                "FROM stock_entries en JOIN products p ON en.product_id = p.product_id " +
                                "UNION ALL " +
                                "SELECT 'OUTGOING' as m_type, p.product_name, ex.quantity, ex.exit_date as action_date " +
                                "FROM stock_exits ex JOIN products p ON ex.product_id = p.product_id " +
                                "ORDER BY action_date DESC LIMIT 30";
            List<StockMovement> movements = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(historySql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movements.add(new StockMovement(
                        rs.getString("m_type"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("action_date")
                    ));
                }
            }
            report.setRecentMovements(movements);

        }
        return report;
    }
}