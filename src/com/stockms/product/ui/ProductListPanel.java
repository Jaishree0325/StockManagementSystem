package com.stockms.product.ui;

import com.stockms.common.exception.StockManagementException;
import com.stockms.product.model.Product;
import com.stockms.product.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Main screen for the Product module: a searchable, sortable table of every
 * product with Add / Edit / Delete / Refresh / Manage Categories actions.
 * Rows whose quantity has fallen to or below the reorder level are
 * highlighted in the table so administrators get an at-a-glance low-stock
 * signal even before the dedicated Alerts module runs.
 */
public class ProductListPanel extends JPanel {

    private final ProductService productService = new ProductService();

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public ProductListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadProducts("");
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadProducts(searchField.getText());
            }
        });
        JButton categoriesButton = new JButton("Manage Categories");
        categoriesButton.addActionListener(this::onManageCategories);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(categoriesButton);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "ID", "SKU", "Name", "Category", "Unit", "Unit Price", "Qty in Stock", "Reorder Level"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                    case 6:
                    case 7:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setAutoCreateRowSorter(true);
        productTable.setRowHeight(24);
        productTable.setDefaultRenderer(Object.class, new LowStockRowRenderer());
        productTable.setDefaultRenderer(Integer.class, new LowStockRowRenderer());

        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Product");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(this::onAdd);
        editButton.addActionListener(this::onEdit);
        deleteButton.addActionListener(this::onDelete);
        refreshButton.addActionListener(e -> loadProducts(searchField.getText()));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onAdd(ActionEvent e) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        ProductFormPanel form = new ProductFormPanel(owner, null);
        form.setVisible(true);
        if (form.isSaved()) {
            loadProducts(searchField.getText());
        }
    }

    private void onEdit(ActionEvent e) {
        Product selected = getSelectedProduct();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this);
        ProductFormPanel form = new ProductFormPanel(owner, selected);
        form.setVisible(true);
        if (form.isSaved()) {
            loadProducts(searchField.getText());
        }
    }

    private void onDelete(ActionEvent e) {
        Product selected = getSelectedProduct();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete product '" + selected.getName() + "' (SKU: " + selected.getSku() + ")?\n"
                        + "This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            productService.deleteProduct(selected.getProductId());
            loadProducts(searchField.getText());
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Unable to Delete Product",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onManageCategories(ActionEvent e) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        CategoryManagerPanel manager = new CategoryManagerPanel(owner);
        manager.setVisible(true);
        // Category names shown in the "Category" column may have changed; refresh regardless.
        loadProducts(searchField.getText());
    }

    private Product getSelectedProduct() {
        int viewRow = productTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = productTable.convertRowIndexToModel(viewRow);
        int productId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            return productService.getProduct(productId);
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void loadProducts(String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Product> products = productService.searchProducts(keyword);
            for (Product product : products) {
                tableModel.addRow(new Object[]{
                        product.getProductId(),
                        product.getSku(),
                        product.getName(),
                        product.getCategoryName() == null ? "" : product.getCategoryName(),
                        product.getUnit(),
                        formatCurrency(product.getUnitPrice()),
                        product.getQuantityInStock(),
                        product.getReorderLevel()
                });
            }
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * Highlights table rows in a light red background when the quantity in
     * stock (column 6) is at or below the reorder level (column 7), giving
     * administrators a quick visual low-stock cue.
     */
    private class LowStockRowRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int modelRow = table.convertRowIndexToModel(row);
            int qty = (Integer) tableModel.getValueAt(modelRow, 6);
            int reorderLevel = (Integer) tableModel.getValueAt(modelRow, 7);
            if (!isSelected) {
                if (qty <= reorderLevel) {
                    c.setBackground(new Color(255, 205, 205));
                } else {
                    c.setBackground(Color.WHITE);
                }
            }
            return c;
        }
    }
}
