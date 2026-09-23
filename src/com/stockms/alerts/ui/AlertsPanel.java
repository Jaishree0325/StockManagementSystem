package com.stockms.alerts.ui;

import com.stockms.alerts.model.AlertRule;
import com.stockms.alerts.service.AlertService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AlertsPanel extends JPanel {

    private JTable alertsTable;
    private AlertService alertService;

    public AlertsPanel() {
        alertService = new AlertService();
        initComponents();
        loadAlerts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Header
        JLabel headerLabel = new JLabel("Active Stock Alerts", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setForeground(new Color(180, 0, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Alerts Table
        alertsTable = new JTable();
        alertsTable.setFillsViewportHeight(true);
        alertsTable.setRowHeight(25);
        alertsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Custom Renderer to highlight rows based on severity
        alertsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                           boolean isSelected, boolean hasFocus, 
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String severity = (String) table.getModel().getValueAt(row, 4); // Column index 4 is Severity
                
                if (!isSelected) {
                    if (severity.contains("OUT OF STOCK")) {
                        c.setBackground(new Color(255, 180, 180)); // Dark Red highlight
                        c.setForeground(Color.BLACK);
                    } else if (severity.contains("VERY LOW")) {
                        c.setBackground(new Color(255, 204, 204)); // Light Red highlight
                        c.setForeground(Color.BLACK);
                    } else if (severity.contains("WARNING")) {
                        c.setBackground(new Color(255, 255, 204)); // Yellow highlight
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        add(new JScrollPane(alertsTable), BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh Alerts");
        refreshBtn.addActionListener(e -> loadAlerts());
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadAlerts() {
        try {
            List<AlertRule> alerts = alertService.getActiveAlerts();
            
            String[] columns = {"Product ID", "Product Name", "Current Stock", "Reorder Threshold", "Severity Level"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table read-only
                }
            };

            for (AlertRule alert : alerts) {
                model.addRow(new Object[]{
                    alert.getProductId(),
                    alert.getProductName(),
                    alert.getCurrentQuantity(),
                    alert.getMinimumThresholdQuantity(),
                    alert.getSeverityLevel()
                });
            }
            
            alertsTable.setModel(model);

            if (alerts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Inventory levels look good! No active alerts.", "All Clear", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load alerts: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}