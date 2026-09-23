package com.stockms.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a single stock item / product record.
 *
 * Maps to the `products` table:
 *   product_id      INT PRIMARY KEY AUTO_INCREMENT
 *   sku             VARCHAR(30) NOT NULL UNIQUE
 *   name            VARCHAR(100) NOT NULL
 *   description     VARCHAR(500)
 *   category_id     INT (FK -> categories.category_id)
 *   unit            VARCHAR(20)            -- e.g. pcs, kg, box
 *   unit_price      DECIMAL(12,2)
 *   quantity_in_stock INT NOT NULL DEFAULT 0
 *   reorder_level   INT NOT NULL DEFAULT 10  -- used by the Alerts module
 *   created_at      DATETIME
 *   updated_at      DATETIME
 *
 * {@code categoryName} is a transient, read-only convenience field populated
 * by {@code ProductDAO} via a JOIN so UI tables can display the category
 * name without a second lookup; it is never written back to the database.
 */
public class Product {

    private int productId;
    private String sku;
    private String name;
    private String description;
    private int categoryId;
    private String categoryName;
    private String unit;
    private BigDecimal unitPrice;
    private int quantityInStock;
    private int reorderLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Product(String sku, String name, String description, int categoryId, String unit,
                    BigDecimal unitPrice, int quantityInStock, int reorderLevel) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** Returns true when quantityInStock has fallen to or below reorderLevel. Used by the Alerts module. */
    public boolean isLowStock() {
        return quantityInStock <= reorderLevel;
    }

    /** Returns true when quantityInStock is exactly zero. */
    public boolean isOutOfStock() {
        return quantityInStock <= 0;
    }

    @Override
    public String toString() {
        return sku + " - " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
