package com.stockms.reports.ui;

import com.stockms.reports.model.ReportData;
import com.stockms.reports.service.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ReportsPanel extends JPanel {

    private JLabel lblTotalValue;
    private JLabel lblTotalProducts;
    private JLabel lblLowStock;
    private JTable historyTable;
    private JTable topSellersTable;
    private ReportService reportService;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ReportsPanel() {
        reportService = new ReportService();
        initComponents();
        loadReportData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Summary Cards Panel (NORTH) ---
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        
        lblTotalValue = createCardLabel("0.00");
        cardsPanel.add(createSummaryCard("Total Inventory Value ($)", lblTotalValue, new Color(220, 255, 220)));
        
        lblTotalProducts = createCardLabel("0");
        cardsPanel.add(createSummaryCard("Total Registered Products", lblTotalProducts, new Color(220, 240, 255)));
        
        lblLowStock = createCardLabel("0");
        cardsPanel.add(createSummaryCard("Items Low on Stock", lblLowStock, new Color(255, 220, 220)));

        add(cardsPanel, BorderLayout.NORTH);

        // --- Data Tables (CENTER) ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // History Table
        historyTable = new JTable();
        historyTable.setFillsViewportHeight(true);
        tabbedPane.addTab("Recent Stock Movements", new JScrollPane(historyTable));

        // Top Sellers Table
        topSellersTable = new JTable();
        topSellersTable.setFillsViewportHeight(true);
        tabbedPane.addTab("Top Selling Products", new JScrollPane(topSellersTable));

        add(tabbedPane, BorderLayout.CENTER);
        
        // Refresh Button (SOUTH)
        JButton btnRefresh = new JButton("Refresh Reports");
        btnRefresh.addActionListener(e -> loadReportData());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createCardLabel(String defaultText) {
        JLabel label = new JLabel(defaultText, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        return label;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        valueLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void loadReportData() {
        try {
            ReportData data = reportService.generateFullReport();

            // Update Cards
            lblTotalValue.setText(String.format("$%.2f", data.getTotalStockValue()));
            lblTotalProducts.setText(String.valueOf(data.getTotalProductsCount()));
            lblLowStock.setText(String.valueOf(data.getLowStockCount()));

            // Update History Table
            String[] historyCols = {"Movement", "Product Name", "Quantity", "Date/Time"};
            DefaultTableModel historyModel = new DefaultTableModel(historyCols, 0);
            for (ReportData.StockMovement m : data.getRecentMovements()) {
                historyModel.addRow(new Object[]{
                    m.getMovementType(), m.getProductName(), m.getQuantity(), 
                    (m.getActionDate() != null) ? sdf.format(m.getActionDate()) : "N/A"
                });
            }
            historyTable.setModel(historyModel);

            // Update Top Sellers Table
            String[] topCols = {"Product Name", "Total Quantity Exited/Sold"};
            DefaultTableModel topModel = new DefaultTableModel(topCols, 0);
            for (ReportData.TopSeller ts : data.getTopSellers()) {
                topModel.addRow(new Object[]{ts.getProductName(), ts.getTotalQuantitySold()});
            }
            topSellersTable.setModel(topModel);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load reports: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}