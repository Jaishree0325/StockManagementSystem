package com.stockms.stockentry.model;

import java.util.Date;

public class StockEntry {
    private int entryId;
    private int productId;
    private int supplierId;
    private int quantity;
    private Date entryDate;
    private String remarks;

    public StockEntry() {
    }

    public StockEntry(int entryId, int productId, int supplierId, int quantity, Date entryDate, String remarks) {
        this.entryId = entryId;
        this.productId = productId;
        this.supplierId = supplierId;
        this.quantity = quantity;
        this.entryDate = entryDate;
        this.remarks = remarks;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}