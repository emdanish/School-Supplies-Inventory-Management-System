package ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import dao.ItemDAO;
import dao.TransactionDAO;
import model.Item;
import model.Transaction;

public class TransactionPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    
    private TransactionDAO transactionDAO;
    private ItemDAO itemDAO;
    
    public TransactionPanel() {
        setLayout(new BorderLayout(10, 10));
        
        transactionDAO = new TransactionDAO();
        itemDAO = new ItemDAO();
        
        initComponents();
        loadTransactionData();
    }
    
    private void initComponents() {
        // Create table model with non-editable cells
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Define table columns
        tableModel.addColumn("ID");
        tableModel.addColumn("Item");
        tableModel.addColumn("Type");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Date");
        
        // Create JTable with the model
        transactionTable = new JTable(tableModel);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        
        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterComboBox = new JComboBox<>(new String[] {
                "All Transactions", 
                "Stock In Transactions", 
                "Stock Out Transactions"
        });
        
        filterComboBox.addActionListener(e -> {
            loadTransactionData();
        });
        
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);
        
        // Add components to the main panel
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadTransactionData() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all transactions
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        
        // Apply filter if needed
        String filterValue = (String) filterComboBox.getSelectedItem();
        
        // Format date for display
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Populate table with transaction data
        for (Transaction transaction : transactions) {
            // Apply filter
            if ("Stock In Transactions".equals(filterValue) && !"IN".equals(transaction.getType())) {
                continue;
            }
            if ("Stock Out Transactions".equals(filterValue) && !"OUT".equals(transaction.getType())) {
                continue;
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
            
            tableModel.addRow(new Object[] {
                    transaction.getTransactionId(),
                    transaction.getItemName(),
                    typeDisplay,
                    transaction.getQuantity(),
                    dateFormat.format(transaction.getDate())
            });
        }
    }
    
    // Method to refresh data (can be called from outside)
    public void refreshData() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadTransactionData();
            }
        });
    }
} 