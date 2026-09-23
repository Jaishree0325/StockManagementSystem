package com.stockms.product.ui;

import com.stockms.common.exception.StockManagementException;
import com.stockms.common.util.ValidationUtil;
import com.stockms.product.model.Category;
import com.stockms.product.model.Product;
import com.stockms.product.service.CategoryService;
import com.stockms.product.service.ProductService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Modal dialog used to both ADD a new {@link Product} and EDIT an existing
 * one. Which mode it runs in is determined by whether a {@link Product} is
 * passed into the constructor (edit mode) or not (add mode).
 */
public class ProductFormPanel extends JDialog {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    private final Product editingProduct; // null when adding a new product
    private boolean saved = false;

    private JTextField skuField;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<Category> categoryCombo;
    private JTextField unitField;
    private JTextField unitPriceField;
    private JTextField quantityField;
    private JTextField reorderLevelField;

    /**
     * @param owner   parent window for modal centering.
     * @param product the product to edit, or null to create a new one.
     */
    public ProductFormPanel(Window owner, Product product) {
        super(owner, product == null ? "Add Product" : "Edit Product", ModalityType.APPLICATION_MODAL);
        this.editingProduct = product;
        initComponents();
        loadCategoriesIntoCombo();
        if (editingProduct != null) {
            populateFieldsFromProduct();
        }
        setSize(480, 460);
        setLocationRelativeTo(owner);
    }

    /** Returns true if the user successfully saved (added or updated) the product. */
    public boolean isSaved() {
        return saved;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("SKU:"), gbc);
        skuField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(skuField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(descScroll, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        JPanel categoryRow = new JPanel(new BorderLayout(5, 0));
        categoryCombo = new JComboBox<>();
        JButton manageCategoriesButton = new JButton("Manage...");
        manageCategoriesButton.addActionListener(this::onManageCategories);
        categoryRow.add(categoryCombo, BorderLayout.CENTER);
        categoryRow.add(manageCategoriesButton, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(categoryRow, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Unit (e.g. pcs, kg):"), gbc);
        unitField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(unitField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Unit Price:"), gbc);
        unitPriceField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(unitPriceField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Quantity in Stock:"), gbc);
        quantityField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(quantityField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Reorder Level:"), gbc);
        reorderLevelField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(reorderLevelField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCategoriesIntoCombo() {
        categoryCombo.removeAllItems();
        try {
            List<Category> categories = categoryService.getAllCategories();
            for (Category category : categories) {
                categoryCombo.addItem(category);
            }
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onManageCategories(ActionEvent e) {
        CategoryManagerPanel manager = new CategoryManagerPanel(this);
        manager.setVisible(true);
        if (manager.isDataChanged()) {
            loadCategoriesIntoCombo();
        }
    }

    private void populateFieldsFromProduct() {
        skuField.setText(editingProduct.getSku());
        nameField.setText(editingProduct.getName());
        descriptionArea.setText(editingProduct.getDescription());
        unitField.setText(editingProduct.getUnit());
        unitPriceField.setText(editingProduct.getUnitPrice().toPlainString());
        quantityField.setText(String.valueOf(editingProduct.getQuantityInStock()));
        reorderLevelField.setText(String.valueOf(editingProduct.getReorderLevel()));

        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            Category item = categoryCombo.getItemAt(i);
            if (item.getCategoryId() == editingProduct.getCategoryId()) {
                categoryCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void onSave(ActionEvent e) {
        String sku = skuField.getText();
        String name = nameField.getText();
        String description = descriptionArea.getText();
        String unit = unitField.getText();

        Category selectedCategory = (Category) categoryCombo.getSelectedItem();
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a category, or create one first via 'Manage...'.",
                    "Category Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidationUtil.isNonNegativeDecimal(unitPriceField.getText())) {
            JOptionPane.showMessageDialog(this, "Unit price must be a valid non-negative number.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!ValidationUtil.isNonNegativeInteger(quantityField.getText())) {
            JOptionPane.showMessageDialog(this, "Quantity in stock must be a valid non-negative whole number.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!ValidationUtil.isNonNegativeInteger(reorderLevelField.getText())) {
            JOptionPane.showMessageDialog(this, "Reorder level must be a valid non-negative whole number.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal unitPrice = new BigDecimal(unitPriceField.getText().trim());
        int quantity = Integer.parseInt(quantityField.getText().trim());
        int reorderLevel = Integer.parseInt(reorderLevelField.getText().trim());

        try {
            if (editingProduct == null) {
                productService.addProduct(sku, name, description, selectedCategory.getCategoryId(), unit,
                        unitPrice, quantity, reorderLevel);
                JOptionPane.showMessageDialog(this, "Product added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                productService.updateProduct(editingProduct.getProductId(), sku, name, description,
                        selectedCategory.getCategoryId(), unit, unitPrice, quantity, reorderLevel);
                JOptionPane.showMessageDialog(this, "Product updated successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            saved = true;
            dispose();
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Unable to Save Product",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
