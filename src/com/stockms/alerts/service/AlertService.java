package com.stockms.alerts.service;

import com.stockms.alerts.model.AlertRule;
import com.stockms.common.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AlertService {

    public List<AlertRule> getActiveAlerts() throws Exception {
        List<AlertRule> alerts = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            /* 
             * This queries the products table. If you have an actual 'alert_rules' table, 
             * you would JOIN it here. For safety/portability, this uses a COALESCE fallback 
             * assuming a default threshold of 10 if a specific threshold isn't set.
             */
            String sql = "SELECT p.product_id, p.product_name, p.quantity, " +
                         "COALESCE((SELECT min_threshold FROM alert_rules a WHERE a.product_id = p.product_id), 10) as threshold " +
                         "FROM products p " +
                         "WHERE p.quantity <= COALESCE((SELECT min_threshold FROM alert_rules a WHERE a.product_id = p.product_id), 10) " +
                         "ORDER BY p.quantity ASC";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    AlertRule rule = new AlertRule();
                    rule.setProductId(rs.getInt("product_id"));
                    rule.setProductName(rs.getString("product_name"));
                    rule.setCurrentQuantity(rs.getInt("quantity"));
                    
                    int threshold = rs.getInt("threshold");
                    rule.setMinimumThresholdQuantity(threshold);
                    
                    // Determine severity dynamically
                    if (rule.getCurrentQuantity() <= 0) {
                        rule.setSeverityLevel("CRITICAL - OUT OF STOCK");
                    } else if (rule.getCurrentQuantity() <= (threshold / 2)) {
                        rule.setSeverityLevel("CRITICAL - VERY LOW");
                    } else {
                        rule.setSeverityLevel("WARNING - NEEDS REORDER");
                    }
                    
                    alerts.add(rule);
                }
            }
        }
        return alerts;
    }
}