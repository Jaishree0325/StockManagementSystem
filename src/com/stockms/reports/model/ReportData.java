package com.stockms.reports.model;

import java.util.List;

public class ReportData {
    private double totalStockValue;
    private int totalProductsCount;
    private int lowStockCount;
    private List<TopSeller> topSellers;
    private List<StockMovement> recentMovements;

    public ReportData() {
    }

    public double getTotalStockValue() { return totalStockValue; }
    public void setTotalStockValue(double totalStockValue) { this.totalStockValue = totalStockValue; }

    public int getTotalProductsCount() { return totalProductsCount; }
    public void setTotalProductsCount(int totalProductsCount) { this.totalProductsCount = totalProductsCount; }

    public int getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }

    public List<TopSeller> getTopSellers() { return topSellers; }
    public void setTopSellers(List<TopSeller> topSellers) { this.topSellers = topSellers; }

    public List<StockMovement> getRecentMovements() { return recentMovements; }
    public void setRecentMovements(List<StockMovement> recentMovements) { this.recentMovements = recentMovements; }

    // Inner class for Top Seller Data
    public static class TopSeller {
        private String productName;
        private int totalQuantitySold;

        public TopSeller(String productName, int totalQuantitySold) {
            this.productName = productName;
            this.totalQuantitySold = totalQuantitySold;
        }

        public String getProductName() { return productName; }
        public int getTotalQuantitySold() { return totalQuantitySold; }
    }

    // Inner class for History Data
    public static class StockMovement {
        private String movementType; // "ENTRY" or "EXIT"
        private String productName;
        private int quantity;
        private java.util.Date actionDate;

        public StockMovement(String movementType, String productName, int quantity, java.util.Date actionDate) {
            this.movementType = movementType;
            this.productName = productName;
            this.quantity = quantity;
            this.actionDate = actionDate;
        }

        public String getMovementType() { return movementType; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public java.util.Date getActionDate() { return actionDate; }
    }
}