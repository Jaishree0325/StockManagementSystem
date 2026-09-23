package com.stockms;

import com.stockms.common.constants.AppConstants;
import com.stockms.common.db.DBConnection;
import com.stockms.product.ui.ProductListPanel;
import com.stockms.supplier.ui.SupplierListPanel;
import com.stockms.stockentry.ui.StockEntryFormPanel;
import com.stockms.stockexit.ui.StockExitFormPanel;
import com.stockms.reports.ui.ReportsPanel;
import com.stockms.alerts.ui.AlertsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Completed Application Entry Point dashboard window. 
 * Connects and displays all 6 operational management modules.
 */
public class Main extends JFrame {

    private final JTabbedPane tabbedPane = new JTabbedPane();

    public Main() {
        super(AppConstants.APP_TITLE + " v" + AppConstants.APP_VERSION);
        initLookAndFeel();
        initMenuBar();
        initBody();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Could not set system look and feel: " + ex.getMessage());
        }
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem testConnectionItem = new JMenuItem("Test Database Connection");
        testConnectionItem.addActionListener(this::onTestConnection);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(testConnectionItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu modulesMenu = new JMenu("Modules");
        JMenuItem productItem = new JMenuItem("Products");
        productItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        JMenuItem supplierItem = new JMenuItem("Suppliers");
        supplierItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        JMenuItem stockEntryItem = new JMenuItem("Stock Entry");
        stockEntryItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        JMenuItem stockExitItem = new JMenuItem("Stock Exit");
        stockExitItem.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        JMenuItem reportsItem = new JMenuItem("Reports Dashboard");
        reportsItem.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        JMenuItem alertsItem = new JMenuItem("System Alerts");
        alertsItem.addActionListener(e -> tabbedPane.setSelectedIndex(5));

        modulesMenu.add(productItem);
        modulesMenu.add(supplierItem);
        modulesMenu.addSeparator();
        modulesMenu.add(stockEntryItem);
        modulesMenu.add(stockExitItem);
        modulesMenu.add(reportsItem);
        modulesMenu.add(alertsItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this::onAbout);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(modulesMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void initBody() {
        tabbedPane.addTab("Products", new ProductListPanel());
        tabbedPane.addTab("Suppliers", new SupplierListPanel());
        tabbedPane.addTab("Stock Entry", new StockEntryFormPanel());
        tabbedPane.addTab("Stock Exit", new StockExitFormPanel());
        tabbedPane.addTab("Reports Dashboard", new ReportsPanel());
        tabbedPane.addTab("System Alerts", new AlertsPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void onTestConnection(ActionEvent e) {
        boolean connected = DBConnection.testConnection();
        if (connected) {
            JOptionPane.showMessageDialog(this,
                    "Successfully connected to the database at:\n" + AppConstants.DB_URL,
                    "Connection Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not connect to the database at:\n" + AppConstants.DB_URL
                            + "\n\nPlease verify the MySQL server is running and that "
                            + "the connection settings in AppConstants.java are correct.",
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAbout(ActionEvent e) {
        JOptionPane.showMessageDialog(this,
                AppConstants.APP_TITLE + "\nVersion " + AppConstants.APP_VERSION
                        + "\n\nHelps organizations maintain accurate records of available stock, "
                        + "incoming materials, outgoing products, and supplier information.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main mainWindow = new Main();
            mainWindow.setVisible(true);
        });
    }
}
