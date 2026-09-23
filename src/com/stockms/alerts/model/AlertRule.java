package com.stockms.alerts.model;

public class AlertRule {
    private int productId;
    private String productName;
    private int currentQuantity;
    private int minimumThresholdQuantity;
    private String severityLevel;

    public AlertRule() {
    }

    public AlertRule(int productId, String productName, int currentQuantity, int minimumThresholdQuantity, String severityLevel) {
        this.productId = productId;
        this.productName = productName;
        this.currentQuantity = currentQuantity;
        this.minimumThresholdQuantity = minimumThresholdQuantity;
        this.severityLevel = severityLevel;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(int currentQuantity) { this.currentQuantity = currentQuantity; }

    public int getMinimumThresholdQuantity() { return minimumThresholdQuantity; }
    public void setMinimumThresholdQuantity(int minimumThresholdQuantity) { this.minimumThresholdQuantity = minimumThresholdQuantity; }

    public String getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }
}