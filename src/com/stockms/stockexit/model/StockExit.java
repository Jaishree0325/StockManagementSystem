package com.stockms.stockexit.model;

import java.util.Date;

public class StockExit {
    private int exitId;
    private int productId;
    private int quantity;
    private Date exitDate;
    private String reason;

    public StockExit() {
    }

    public StockExit(int exitId, int productId, int quantity, Date exitDate, String reason) {
        this.exitId = exitId;
        this.productId = productId;
        this.quantity = quantity;
        this.exitDate = exitDate;
        this.reason = reason;
    }

    public int getExitId() {
        return exitId;
    }

    public void setExitId(int exitId) {
        this.exitId = exitId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getExitDate() {
        return exitDate;
    }

    public void setExitDate(Date exitDate) {
        this.exitDate = exitDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}