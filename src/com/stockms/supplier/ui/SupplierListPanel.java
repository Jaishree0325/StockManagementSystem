package com.stockms.supplier.ui;

import com.stockms.common.exception.StockManagementException;
import com.stockms.supplier.model.Supplier;
import com.stockms.supplier.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Main screen for the Supplier module: a searchable table of every vendor
 * with Add / Edit / Delete / Refresh actions.
 */
public class SupplierListPanel extends JPanel {

    private final SupplierService supplierService = new SupplierService();

    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public SupplierListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadSuppliers("");
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Supplier Management");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadSuppliers(searchField.getText());
            }
        });
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Name", "Contact Person", "Phone", "E-mail", "Address"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }
        };
        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.setAutoCreateRowSorter(true);
        supplierTable.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(supplierTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Supplier");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(this::onAdd);
        editButton.addActionListener(this::onEdit);
        deleteButton.addActionListener(this::onDelete);
        refreshButton.addActionListener(e -> loadSuppliers(searchField.getText()));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onAdd(ActionEvent e) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        SupplierFormPanel form = new SupplierFormPanel(owner, null);
        form.setVisible(true);
        if (form.isSaved()) {
            loadSuppliers(searchField.getText());
        }
    }

    private void onEdit(ActionEvent e) {
        Supplier selected = getSelectedSupplier();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this);
        SupplierFormPanel form = new SupplierFormPanel(owner, selected);
        form.setVisible(true);
        if (form.isSaved()) {
            loadSuppliers(searchField.getText());
        }
    }

    private void onDelete(ActionEvent e) {
        Supplier selected = getSelectedSupplier();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete supplier '" + selected.getName() + "'? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            supplierService.deleteSupplier(selected.getSupplierId());
            loadSuppliers(searchField.getText());
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Unable to Delete Supplier",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Supplier getSelectedSupplier() {
        int viewRow = supplierTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = supplierTable.convertRowIndexToModel(viewRow);
        int supplierId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            return supplierService.getSupplier(supplierId);
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void loadSuppliers(String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Supplier> suppliers = supplierService.searchSuppliers(keyword);
            for (Supplier supplier : suppliers) {
                tableModel.addRow(new Object[]{
                        supplier.getSupplierId(),
                        supplier.getName(),
                        supplier.getContactPerson(),
                        supplier.getPhone(),
                        supplier.getEmail(),
                        supplier.getAddress()
                });
            }
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load suppliers: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
