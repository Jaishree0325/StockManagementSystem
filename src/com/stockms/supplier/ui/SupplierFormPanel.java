package com.stockms.supplier.ui;

import com.stockms.common.exception.StockManagementException;
import com.stockms.common.util.ValidationUtil;
import com.stockms.supplier.model.Supplier;
import com.stockms.supplier.service.SupplierService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Modal dialog used to both ADD a new {@link Supplier} and EDIT an existing
 * one. Which mode it runs in is determined by whether a {@link Supplier} is
 * passed into the constructor (edit mode) or not (add mode).
 */
public class SupplierFormPanel extends JDialog {

    private final SupplierService supplierService = new SupplierService();

    private final Supplier editingSupplier; // null when adding a new supplier
    private boolean saved = false;

    private JTextField nameField;
    private JTextField contactPersonField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;

    /**
     * @param owner    parent window for modal centering.
     * @param supplier the supplier to edit, or null to create a new one.
     */
    public SupplierFormPanel(Window owner, Supplier supplier) {
        super(owner, supplier == null ? "Add Supplier" : "Edit Supplier", ModalityType.APPLICATION_MODAL);
        this.editingSupplier = supplier;
        initComponents();
        if (editingSupplier != null) {
            populateFieldsFromSupplier();
        }
        setSize(460, 400);
        setLocationRelativeTo(owner);
    }

    /** Returns true if the user successfully saved (added or updated) the supplier. */
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
        formPanel.add(new JLabel("Supplier Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Contact Person:"), gbc);
        contactPersonField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(contactPersonField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(phoneField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("E-mail:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Address:"), gbc);
        addressArea = new JTextArea(4, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(addressScroll, gbc);

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

    private void populateFieldsFromSupplier() {
        nameField.setText(editingSupplier.getName());
        contactPersonField.setText(editingSupplier.getContactPerson());
        phoneField.setText(editingSupplier.getPhone());
        emailField.setText(editingSupplier.getEmail());
        addressArea.setText(editingSupplier.getAddress());
    }

    private void onSave(ActionEvent e) {
        String name = nameField.getText();
        String contactPerson = contactPersonField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String address = addressArea.getText();

        if (ValidationUtil.isNotEmpty(phone) && !ValidationUtil.isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this,
                    "Phone number format is invalid. Use digits, spaces, '+', '-', or parentheses only.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ValidationUtil.isNotEmpty(email) && !ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "E-mail address format is invalid.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (editingSupplier == null) {
                supplierService.addSupplier(name, contactPerson, phone, email, address);
                JOptionPane.showMessageDialog(this, "Supplier added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                supplierService.updateSupplier(editingSupplier.getSupplierId(), name, contactPerson, phone,
                        email, address);
                JOptionPane.showMessageDialog(this, "Supplier updated successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            saved = true;
            dispose();
        } catch (StockManagementException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Unable to Save Supplier",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
