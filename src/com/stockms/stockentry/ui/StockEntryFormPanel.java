package com.stockms.stockentry.ui;

import com.stockms.common.db.DBConnection;
import com.stockms.stockentry.model.StockEntry;
import com.stockms.stockentry.service.StockEntryService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class StockEntryFormPanel extends JPanel {

    private JComboBox<ComboItem> productCombo;
    private JComboBox<ComboItem> supplierCombo;
    private JTextField quantityField;
    private JTextField remarksField;
    private JButton submitButton;
    private StockEntryService service;

    public StockEntryFormPanel() {
        service = new StockEntryService();
        initComponents();
        loadDropdownData();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        productCombo = new JComboBox<>();
        supplierCombo = new JComboBox<>();
        quantityField = new JTextField(15);
        remarksField = new JTextField(15);
        submitButton = new JButton("Submit Stock Entry");

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Select Product:"), gbc);
        gbc.gridx = 1;
        add(productCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Select Supplier:"), gbc);
        gbc.gridx = 1;
        add(supplierCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Remarks:"), gbc);
        gbc.gridx = 1;
        add(remarksField, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });
    }

    private void loadDropdownData() {
        try (Connection conn = DBConnection.getConnection()) {
            // Load Products
            String prodSql = "SELECT product_id, product_name FROM products";
            try (PreparedStatement ps = conn.prepareStatement(prodSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productCombo.addItem(new ComboItem(rs.getInt("product_id"), rs.getString("product_name")));
                }
            }

            // Load Suppliers
            String suppSql = "SELECT supplier_id, supplier_name FROM suppliers";
            try (PreparedStatement ps = conn.prepareStatement(suppSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    supplierCombo.addItem(new ComboItem(rs.getInt("supplier_id"), rs.getString("supplier_name")));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dropdowns: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSubmit() {
        try {
            ComboItem selectedProduct = (ComboItem) productCombo.getSelectedItem();
            ComboItem selectedSupplier = (ComboItem) supplierCombo.getSelectedItem();
            
            if (selectedProduct == null || selectedSupplier == null) {
                JOptionPane.showMessageDialog(this, "Please select a product and supplier.");
                return;
            }

            int qty = Integer.parseInt(quantityField.getText().trim());
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
                return;
            }

            StockEntry entry = new StockEntry();
            entry.setProductId(selectedProduct.getId());
            entry.setSupplierId(selectedSupplier.getId());
            entry.setQuantity(qty);
            entry.setEntryDate(new Date());
            entry.setRemarks(remarksField.getText().trim());

            boolean success = service.processStockEntry(entry);
            if (success) {
                JOptionPane.showMessageDialog(this, "Stock entry processed successfully!");
                quantityField.setText("");
                remarksField.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric quantity.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transaction Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper class for JComboBox items
    private static class ComboItem {
        private int id;
        private String name;

        public ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        
        @Override
        public String toString() { return id + " - " + name; }
    }
}