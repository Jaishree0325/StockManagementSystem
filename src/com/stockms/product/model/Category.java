package com.stockms.product.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a product category (e.g. "Electronics", "Stationery",
 * "Raw Materials"). Categories group {@link Product} records for
 * organization, filtering, and reporting purposes.
 *
 * Maps to the `categories` table:
 *   category_id   INT PRIMARY KEY AUTO_INCREMENT
 *   name          VARCHAR(100) NOT NULL UNIQUE
 *   description   VARCHAR(500)
 *   created_at    DATETIME
 */
public class Category {

    private int categoryId;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(int categoryId, String name, String description, LocalDateTime createdAt) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        // Used directly by JComboBox rendering in the Product form.
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return categoryId == category.categoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }
}
