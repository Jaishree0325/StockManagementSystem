package com.stockms.product.ui;

import com.stockms.common.exception.StockManagementException;
import com.stockms.product.model.Category;
import com.stockms.product.service.CategoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Modal dialog providing full CRUD management of {@link Category} records.
 * Opened from {@link ProductListPanel} via a "Manage Categories" button, and
 * also used by {@link ProductFormPanel} to populate its category combo box.
 */
public class CategoryManagerPanel extends JDialog {

    private final CategoryService categoryService = new CategoryService();

    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton clearButton;
    private JButton deleteButton;

    private Integer selectedCategoryId = null;
    private boolean dataChanged = false;

    public CategoryManagerPanel(Window owner) {
        super(owner, "Manage Categories", ModalityType.APPLICATION_MODAL);
        initComponents();
        loadCategories();
        setSize(600, 420);
        setLocationRelativeTo(owner);
    }

    /** Returns true if any category was added, edited, or deleted while this dialog was open. */
    public boolean isDataChanged() {
        return dataChanged;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTableRowSelected();
            }
        });
        JScrollPane tableScroll = new JScrollPane(categoryTable);
        tableScroll.setPreferredSize(new Dimension(560, 200));
        add(tableScroll, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Category Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(25);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        descriptionArea = new JTextArea(3, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(descScroll, gbc);

        add(formPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        clearButton = new JButton("Clear / New");
        deleteButton = new JButton("Delete Selected");
        JButton closeButton = new JButton("Close");

        saveButton.addActionListener(this::onSave);
        clearButton.addActionListener(this::onClear);
        deleteButton.addActionListener(this::onDelete);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.add(formPanel, BorderLayout.CENTER);
        southWrapper.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().remove(formPanel);
        add(southWrapper, BorderLayout.SOUTH);
    }

    private void onTableRowSelected() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectedCategoryId = (Integer) tableModel.getValueAt(row, 0);
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        descriptionArea.setText(String.valueOf(tableModel.getValueAt(row, 2)));
    }

    private void onSave(ActionEvent e) {
        String name = nameField.getText();
        String description = descriptionArea.getText();
        try {
            if (selectedCategoryId == null) {
                categoryService.addCategory(name, description);
                JOptionPane.showMessageDialog(this, "Category added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                categoryService.updateCategory(selectedCategoryId, name, description);
                JOptionPane.showMessageDialog(this, "Category updated successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            dataChanged = true;
            clearForm();
            loadCategories();
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Unable to Save Category",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onClear(ActionEvent e) {
        clearForm();
    }

    private void onDelete(ActionEvent e) {
        int row = categoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int categoryId = (Integer) tableModel.getValueAt(row, 0);
        String name = String.valueOf(tableModel.getValueAt(row, 1));
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete category '" + name + "'? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            categoryService.deleteCategory(categoryId);
            dataChanged = true;
            clearForm();
            loadCategories();
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Unable to Delete Category",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedCategoryId = null;
        nameField.setText("");
        descriptionArea.setText("");
        categoryTable.clearSelection();
    }

    private void loadCategories() {
        tableModel.setRowCount(0);
        try {
            List<Category> categories = categoryService.getAllCategories();
            for (Category category : categories) {
                tableModel.addRow(new Object[]{
                        category.getCategoryId(),
                        category.getName(),
                        category.getDescription()
                });
            }
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
