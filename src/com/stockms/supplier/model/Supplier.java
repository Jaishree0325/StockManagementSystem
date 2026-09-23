package com.stockms.supplier.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a vendor / supplier who provides incoming stock.
 *
 * Maps to the `suppliers` table:
 *   supplier_id     INT PRIMARY KEY AUTO_INCREMENT
 *   name            VARCHAR(100) NOT NULL
 *   contact_person  VARCHAR(100)
 *   phone           VARCHAR(20)
 *   email           VARCHAR(100)
 *   address         VARCHAR(250)
 *   created_at      DATETIME
 *   updated_at      DATETIME
 */
public class Supplier {

    private int supplierId;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Supplier() {
    }

    public Supplier(String name, String contactPerson, String phone, String email, String address) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Supplier)) return false;
        Supplier supplier = (Supplier) o;
        return supplierId == supplier.supplierId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierId);
    }
}
