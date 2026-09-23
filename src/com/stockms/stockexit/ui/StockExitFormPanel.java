package com.stockms.stockexit.ui;

import com.stockms.common.db.DBConnection;
import com.stockms.stockexit.model.StockExit;
import com.stockms.stockexit.service.StockExitService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class StockExitFormPanel extends JPanel {

    private JComboBox<ComboItem> productCombo;
    private JTextField quantityField;
    private JTextField reasonField;
    private JButton submitButton;
    private StockExitService service;

    public StockExitFormPanel() {
        service = new StockExitService();
        initComponents();
        loadDropdownData();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        productCombo = new JComboBox<>();
        quantityField = new JTextField(15);
        reasonField = new JTextField(15);
        submitButton = new JButton("Submit Stock Exit");

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Select Product:"), gbc);
        gbc.gridx = 1;
        add(productCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Quantity to Deduct:"), gbc);
        gbc.gridx = 1;
        add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Reason (e.g., Sale, Damage):"), gbc);
        gbc.gridx = 1;
        add(reasonField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
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
            String prodSql = "SELECT product_id, product_name FROM products";
            try (PreparedStatement ps = conn.prepareStatement(prodSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productCombo.addItem(new ComboItem(rs.getInt("product_id"), rs.getString("product_name")));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSubmit() {
        try {
            ComboItem selectedProduct = (ComboItem) productCombo.getSelectedItem();
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, "Please select a product.");
                return;
            }

            int qty = Integer.parseInt(quantityField.getText().trim());
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
                return;
            }

            StockExit exit = new StockExit();
            exit.setProductId(selectedProduct.getId());
            exit.setQuantity(qty);
            exit.setExitDate(new Date());
            exit.setReason(reasonField.getText().trim());

            boolean success = service.processStockExit(exit);
            if (success) {
                JOptionPane.showMessageDialog(this, "Stock exit processed successfully!");
                quantityField.setText("");
                reasonField.setText("");
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