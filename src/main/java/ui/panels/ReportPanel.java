package ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import dao.CategoryDAO;
import dao.ItemDAO;
import dao.SupplierDAO;
import dao.TransactionDAO;
import model.Category;
import model.Item;
import model.Supplier;
import model.Transaction;

public class ReportPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JTabbedPane tabbedPane;
    
    // Inventory report components
    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;
    private JLabel totalItemsLabel;
    private JLabel totalValueLabel;
    private JLabel lowStockItemsLabel;
    private JLabel outOfStockItemsLabel;
    
    // Transaction report components
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    private JLabel totalTransactionsLabel;
    private JLabel stockInLabel;
    private JLabel stockOutLabel;
    
    private ItemDAO itemDAO;
    private CategoryDAO categoryDAO;
    private SupplierDAO supplierDAO;
    private TransactionDAO transactionDAO;
    
    public ReportPanel() {
        setLayout(new BorderLayout());
        
        itemDAO = new ItemDAO();
        categoryDAO = new CategoryDAO();
        supplierDAO = new SupplierDAO();
        transactionDAO = new TransactionDAO();
        
        initComponents();
        loadReportData();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Create Inventory Report Panel
        JPanel inventoryReportPanel = createInventoryReportPanel();
        
        // Create Transaction Report Panel
        JPanel transactionReportPanel = createTransactionReportPanel();
        
        // Add tabs
        tabbedPane.addTab("Inventory Report", inventoryReportPanel);
        tabbedPane.addTab("Transaction Report", transactionReportPanel);
        
        // Add tabbed pane to main panel
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createInventoryReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model with non-editable cells
        inventoryTableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Define table columns
        inventoryTableModel.addColumn("ID");
        inventoryTableModel.addColumn("Name");
        inventoryTableModel.addColumn("Category");
        inventoryTableModel.addColumn("Quantity");
        inventoryTableModel.addColumn("Supplier");
        inventoryTableModel.addColumn("Unit Price");
        inventoryTableModel.addColumn("Total Value");
        inventoryTableModel.addColumn("Status");
        
        // Create JTable with the model
        inventoryTable = new JTable(inventoryTableModel);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        
        // Create summary panel with GridBagLayout
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Total Items
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        summaryPanel.add(new JLabel("Total Items:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        totalItemsLabel = new JLabel("0");
        summaryPanel.add(totalItemsLabel, gbc);
        
        // Total Value
        gbc.gridx = 0;
        gbc.gridy = 1;
        summaryPanel.add(new JLabel("Total Value:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        totalValueLabel = new JLabel("$0.00");
        summaryPanel.add(totalValueLabel, gbc);
        
        // Low Stock Items
        gbc.gridx = 2;
        gbc.gridy = 0;
        summaryPanel.add(new JLabel("Low Stock Items:"), gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 0;
        lowStockItemsLabel = new JLabel("0");
        summaryPanel.add(lowStockItemsLabel, gbc);
        
        // Out of Stock Items
        gbc.gridx = 2;
        gbc.gridy = 1;
        summaryPanel.add(new JLabel("Out of Stock Items:"), gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 1;
        outOfStockItemsLabel = new JLabel("0");
        summaryPanel.add(outOfStockItemsLabel, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportInventoryReportToCSV();
            }
        });
        buttonPanel.add(exportButton);
        
        // Add components to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTransactionReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model with non-editable cells
        transactionTableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Define table columns
        transactionTableModel.addColumn("ID");
        transactionTableModel.addColumn("Item");
        transactionTableModel.addColumn("Type");
        transactionTableModel.addColumn("Quantity");
        transactionTableModel.addColumn("Date");
        
        // Create JTable with the model
        transactionTable = new JTable(transactionTableModel);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        
        // Create summary panel with GridBagLayout
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Total Transactions
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        summaryPanel.add(new JLabel("Total Transactions:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        totalTransactionsLabel = new JLabel("0");
        summaryPanel.add(totalTransactionsLabel, gbc);
        
        // Stock In Transactions
        gbc.gridx = 2;
        gbc.gridy = 0;
        summaryPanel.add(new JLabel("Stock In:"), gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 0;
        stockInLabel = new JLabel("0");
        summaryPanel.add(stockInLabel, gbc);
        
        // Stock Out Transactions
        gbc.gridx = 2;
        gbc.gridy = 1;
        summaryPanel.add(new JLabel("Stock Out:"), gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 1;
        stockOutLabel = new JLabel("0");
        summaryPanel.add(stockOutLabel, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportTransactionReportToCSV();
            }
        });
        buttonPanel.add(exportButton);
        
        // Add components to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadReportData() {
        loadInventoryReport();
        loadTransactionReport();
    }
    
    private void loadInventoryReport() {
        // Clear table
        inventoryTableModel.setRowCount(0);
        
        // Get all items
        List<Item> items = itemDAO.getAllItems();
        
        int totalItems = items.size();
        double totalValue = 0;
        int lowStockItems = 0;
        int outOfStockItems = 0;
        
        // Populate table with item data
        for (Item item : items) {
            Category category = categoryDAO.getCategoryById(item.getCategoryId());
            Supplier supplier = supplierDAO.getSupplierById(item.getSupplierId());
            
            String status = "In Stock";
            if (item.isOutOfStock()) {
                status = "Out of Stock";
                outOfStockItems++;
            } else if (item.isLowStock()) {
                status = "Low Stock";
                lowStockItems++;
            }
            
            double totalItemValue = item.getUnitPrice() * item.getQuantity();
            totalValue += totalItemValue;
            
            inventoryTableModel.addRow(new Object[] {
                    item.getItemId(),
                    item.getItemName(),
                    category != null ? category.getCategoryName() : "Unknown",
                    item.getQuantity(),
                    supplier != null ? supplier.getSupplierName() : "Unknown",
                    String.format("$%.2f", item.getUnitPrice()),
                    String.format("$%.2f", totalItemValue),
                    status
            });
        }
        
        // Update summary labels
        totalItemsLabel.setText(String.valueOf(totalItems));
        totalValueLabel.setText(String.format("$%.2f", totalValue));
        lowStockItemsLabel.setText(String.valueOf(lowStockItems));
        outOfStockItemsLabel.setText(String.valueOf(outOfStockItems));
    }
    
    private void loadTransactionReport() {
        // Clear table
        transactionTableModel.setRowCount(0);
        
        // Get all transactions
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        
        int totalTransactions = transactions.size();
        int stockInCount = 0;
        int stockOutCount = 0;
        
        // Format date for display
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Populate table with transaction data
        for (Transaction transaction : transactions) {
            // Count transaction types
            if ("IN".equals(transaction.getType())) {
                stockInCount++;
            } else if ("OUT".equals(transaction.getType())) {
                stockOutCount++;
            }
            
            // Get item name if not already set in transaction
            if (transaction.getItemName() == null) {
                Item item = itemDAO.getItemById(transaction.getItemId());
                if (item != null) {
                    transaction.setItemName(item.getItemName());
                } else {
                    transaction.setItemName("Unknown Item");
                }
            }
            
            // Format transaction type for display
            String typeDisplay = "Stock In";
            if ("OUT".equals(transaction.getType())) {
                typeDisplay = "Stock Out";
            }
            
            transactionTableModel.addRow(new Object[] {
                    transaction.getTransactionId(),
                    transaction.getItemName(),
                    typeDisplay,
                    transaction.getQuantity(),
                    dateFormat.format(transaction.getDate())
            });
        }
        
        // Update summary labels
        totalTransactionsLabel.setText(String.valueOf(totalTransactions));
        stockInLabel.setText(String.valueOf(stockInCount));
        stockOutLabel.setText(String.valueOf(stockOutCount));
    }
    
    private void exportInventoryReportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Inventory Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        
        // Set default filename with timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFileName = "Inventory_Report_" + dateFormat.format(new Date()) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Ensure file has .csv extension
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                // Write header
                writer.append("ID,Name,Category,Quantity,Supplier,Unit Price,Total Value,Status\n");
                
                // Write data rows
                for (int i = 0; i < inventoryTableModel.getRowCount(); i++) {
                    for (int j = 0; j < inventoryTableModel.getColumnCount(); j++) {
                        Object value = inventoryTableModel.getValueAt(i, j);
                        writer.append(value != null ? value.toString() : "");
                        if (j < inventoryTableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                // Write summary
                writer.append("\nSummary:\n");
                writer.append("Total Items," + totalItemsLabel.getText() + "\n");
                writer.append("Total Value," + totalValueLabel.getText() + "\n");
                writer.append("Low Stock Items," + lowStockItemsLabel.getText() + "\n");
                writer.append("Out of Stock Items," + outOfStockItemsLabel.getText() + "\n");
                
                writer.flush();
                
                JOptionPane.showMessageDialog(this, 
                        "Report exported successfully to " + file.getAbsolutePath(), 
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                        "Error exporting report: " + e.getMessage(), 
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void exportTransactionReportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Transaction Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        
        // Set default filename with timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFileName = "Transaction_Report_" + dateFormat.format(new Date()) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Ensure file has .csv extension
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                // Write header
                writer.append("ID,Item,Type,Quantity,Date\n");
                
                // Write data rows
                for (int i = 0; i < transactionTableModel.getRowCount(); i++) {
                    for (int j = 0; j < transactionTableModel.getColumnCount(); j++) {
                        Object value = transactionTableModel.getValueAt(i, j);
                        writer.append(value != null ? value.toString() : "");
                        if (j < transactionTableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                // Write summary
                writer.append("\nSummary:\n");
                writer.append("Total Transactions," + totalTransactionsLabel.getText() + "\n");
                writer.append("Stock In," + stockInLabel.getText() + "\n");
                writer.append("Stock Out," + stockOutLabel.getText() + "\n");
                
                writer.flush();
                
                JOptionPane.showMessageDialog(this, 
                        "Report exported successfully to " + file.getAbsolutePath(), 
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                        "Error exporting report: " + e.getMessage(), 
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    // Method to refresh data (can be called from outside)
    public void refreshData() {
        loadReportData();
    }
} 