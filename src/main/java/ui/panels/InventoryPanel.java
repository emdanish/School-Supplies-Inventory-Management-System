package ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import dao.CategoryDAO;
import dao.ItemDAO;
import dao.SupplierDAO;
import dao.TransactionDAO;
import model.Category;
import model.Item;
import model.Supplier;
import model.Transaction;

public class InventoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<Supplier> supplierComboBox;
    
    private JTextField nameField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField reorderLevelField;
    
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton addStockButton;
    private JButton removeStockButton;
    
    private ItemDAO itemDAO;
    private CategoryDAO categoryDAO;
    private SupplierDAO supplierDAO;
    private TransactionDAO transactionDAO;
    
    private Item selectedItem;
    
    public InventoryPanel() {
        setLayout(new BorderLayout(10, 10));
        
        itemDAO = new ItemDAO();
        categoryDAO = new CategoryDAO();
        supplierDAO = new SupplierDAO();
        transactionDAO = new TransactionDAO();
        
        initComponents();
        loadItemData();
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
        tableModel.addColumn("Name");
        tableModel.addColumn("Category");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Supplier");
        tableModel.addColumn("Price");
        tableModel.addColumn("Reorder Level");
        tableModel.addColumn("Status");
        
        // Create JTable with the model
        itemTable = new JTable(tableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = itemTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        populateFormFields(selectedRow);
                    }
                }
            }
        });
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(itemTable);
        
        // Create search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        filterComboBox = new JComboBox<>(new String[] {
                "All Items", "Low Stock Items", "Out of Stock Items"
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchItems();
            }
        });
        
        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterItems();
            }
        });
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(filterComboBox);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Category combo box
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        categoryComboBox = new JComboBox<>();
        loadCategories();
        formPanel.add(categoryComboBox, gbc);
        
        // Quantity field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Quantity:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        quantityField = new JTextField(10);
        formPanel.add(quantityField, gbc);
        
        // Supplier combo box
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Supplier:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        supplierComboBox = new JComboBox<>();
        loadSuppliers();
        formPanel.add(supplierComboBox, gbc);
        
        // Price field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Unit Price:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        priceField = new JTextField(10);
        formPanel.add(priceField, gbc);
        
        // Reorder Level field
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Reorder Level:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        reorderLevelField = new JTextField(10);
        formPanel.add(reorderLevelField, gbc);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        addStockButton = new JButton("Add Stock");
        removeStockButton = new JButton("Remove Stock");
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateItem();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        addStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStock();
            }
        });
        
        removeStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeStock();
            }
        });
        
        // Initially disable update and delete buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        addStockButton.setEnabled(false);
        removeStockButton.setEnabled(false);
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(addStockButton);
        buttonsPanel.add(removeStockButton);
        
        // Create input panel to hold form and buttons
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(formPanel, BorderLayout.CENTER);
        inputPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Add components to the main panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void loadItemData() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all items
        List<Item> items = itemDAO.getAllItems();
        
        // Populate table with item data
        for (Item item : items) {
            Category category = categoryDAO.getCategoryById(item.getCategoryId());
            Supplier supplier = supplierDAO.getSupplierById(item.getSupplierId());
            
            String status = "In Stock";
            if (item.isOutOfStock()) {
                status = "Out of Stock";
            } else if (item.isLowStock()) {
                status = "Low Stock";
            }
            
            tableModel.addRow(new Object[] {
                    item.getItemId(),
                    item.getItemName(),
                    category != null ? category.getCategoryName() : "Unknown",
                    item.getQuantity(),
                    supplier != null ? supplier.getSupplierName() : "Unknown",
                    item.getUnitPrice(),
                    item.getReorderLevel(),
                    status
            });
        }
    }
    
    private void loadCategories() {
        categoryComboBox.removeAllItems();
        
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category category : categories) {
            categoryComboBox.addItem(category);
        }
    }
    
    private void loadSuppliers() {
        supplierComboBox.removeAllItems();
        
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier supplier : suppliers) {
            supplierComboBox.addItem(supplier);
        }
    }
    
    private void populateFormFields(int selectedRow) {
        // Get item ID from the selected row
        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Get the corresponding Item object
        selectedItem = itemDAO.getItemById(itemId);
        
        // Populate form fields
        if (selectedItem != null) {
            nameField.setText(selectedItem.getItemName());
            quantityField.setText(String.valueOf(selectedItem.getQuantity()));
            priceField.setText(String.valueOf(selectedItem.getUnitPrice()));
            reorderLevelField.setText(String.valueOf(selectedItem.getReorderLevel()));
            
            // Select the right category in combo box
            Category category = categoryDAO.getCategoryById(selectedItem.getCategoryId());
            if (category != null) {
                for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                    Category item = categoryComboBox.getItemAt(i);
                    if (item.getCategoryId() == category.getCategoryId()) {
                        categoryComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Select the right supplier in combo box
            Supplier supplier = supplierDAO.getSupplierById(selectedItem.getSupplierId());
            if (supplier != null) {
                for (int i = 0; i < supplierComboBox.getItemCount(); i++) {
                    Supplier item = supplierComboBox.getItemAt(i);
                    if (item.getSupplierId() == supplier.getSupplierId()) {
                        supplierComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Enable update and delete buttons
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
            addStockButton.setEnabled(true);
            removeStockButton.setEnabled(true);
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        quantityField.setText("");
        priceField.setText("");
        reorderLevelField.setText("");
        if (categoryComboBox.getItemCount() > 0) {
            categoryComboBox.setSelectedIndex(0);
        }
        if (supplierComboBox.getItemCount() > 0) {
            supplierComboBox.setSelectedIndex(0);
        }
        
        selectedItem = null;
        itemTable.clearSelection();
        
        // Disable update and delete buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        addStockButton.setEnabled(false);
        removeStockButton.setEnabled(false);
    }
    
    private void addItem() {
        // Validate form fields
        if (!validateForm()) {
            return;
        }
        
        // Get values from form fields
        String name = nameField.getText();
        Category category = (Category) categoryComboBox.getSelectedItem();
        Supplier supplier = (Supplier) supplierComboBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());
        int reorderLevel = Integer.parseInt(reorderLevelField.getText());
        
        // Create new Item object
        Item item = new Item(name, category.getCategoryId(), quantity, supplier.getSupplierId(), price, reorderLevel);
        
        // Add item to database
        if (itemDAO.addItem(item)) {
            JOptionPane.showMessageDialog(this, "Item added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Create transaction record for initial stock
            if (quantity > 0) {
                Transaction transaction = new Transaction(item.getItemId(), "IN", quantity);
                transactionDAO.addTransaction(transaction);
            }
            
            // Reload item data
            loadItemData();
            
            // Clear form
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateItem() {
        // Check if an item is selected
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "No item selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate form fields
        if (!validateForm()) {
            return;
        }
        
        // Get values from form fields
        String name = nameField.getText();
        Category category = (Category) categoryComboBox.getSelectedItem();
        Supplier supplier = (Supplier) supplierComboBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());
        int reorderLevel = Integer.parseInt(reorderLevelField.getText());
        
        // Update the Item object
        selectedItem.setItemName(name);
        selectedItem.setCategoryId(category.getCategoryId());
        selectedItem.setSupplierId(supplier.getSupplierId());
        selectedItem.setQuantity(quantity);
        selectedItem.setUnitPrice(price);
        selectedItem.setReorderLevel(reorderLevel);
        
        // Update item in database
        if (itemDAO.updateItem(selectedItem)) {
            JOptionPane.showMessageDialog(this, "Item updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload item data
            loadItemData();
            
            // Clear form
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteItem() {
        // Check if an item is selected
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "No item selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this item?\nThis action cannot be undone.", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Delete item from database
            if (itemDAO.deleteItem(selectedItem.getItemId())) {
                JOptionPane.showMessageDialog(this, "Item deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reload item data
                loadItemData();
                
                // Clear form
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete item", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addStock() {
        // Check if an item is selected
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "No item selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Ask for quantity to add
        String input = JOptionPane.showInputDialog(this, "Enter quantity to add:", "Add Stock", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.isEmpty()) {
            return;
        }
        
        try {
            int quantity = Integer.parseInt(input);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create transaction for adding stock
            Transaction transaction = new Transaction(selectedItem.getItemId(), "IN", quantity);
            
            // Add transaction to database
            if (transactionDAO.addTransaction(transaction)) {
                JOptionPane.showMessageDialog(this, "Stock added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reload item data
                loadItemData();
                
                // Clear form
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add stock", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeStock() {
        // Check if an item is selected
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "No item selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Ask for quantity to remove
        String input = JOptionPane.showInputDialog(this, "Enter quantity to remove:", "Remove Stock", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.isEmpty()) {
            return;
        }
        
        try {
            int quantity = Integer.parseInt(input);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if trying to remove more than available
            if (quantity > selectedItem.getQuantity()) {
                int choice = JOptionPane.showConfirmDialog(this, 
                        "The quantity you are trying to remove exceeds the available stock.\nDo you want to remove all available stock?", 
                        "Confirm Removal", JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                    quantity = selectedItem.getQuantity();
                } else {
                    return;
                }
            }
            
            // Create transaction for removing stock
            Transaction transaction = new Transaction(selectedItem.getItemId(), "OUT", quantity);
            
            // Add transaction to database
            if (transactionDAO.addTransaction(transaction)) {
                JOptionPane.showMessageDialog(this, "Stock removed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reload item data
                loadItemData();
                
                // Clear form
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove stock", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter item name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        // Validate category
        if (categoryComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a category", "Validation Error", JOptionPane.ERROR_MESSAGE);
            categoryComboBox.requestFocus();
            return false;
        }
        
        // Validate supplier
        if (supplierComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a supplier", "Validation Error", JOptionPane.ERROR_MESSAGE);
            supplierComboBox.requestFocus();
            return false;
        }
        
        // Validate quantity
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                quantityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
            quantityField.requestFocus();
            return false;
        }
        
        // Validate price
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than zero", "Validation Error", JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price", "Validation Error", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        // Validate reorder level
        try {
            int reorderLevel = Integer.parseInt(reorderLevelField.getText());
            if (reorderLevel < 0) {
                JOptionPane.showMessageDialog(this, "Reorder level cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                reorderLevelField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid reorder level", "Validation Error", JOptionPane.ERROR_MESSAGE);
            reorderLevelField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void searchItems() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            // If search field is empty, reload all items
            loadItemData();
        } else {
            // Search items by name
            List<Item> items = itemDAO.searchItemsByName(searchTerm);
            
            // Clear table
            tableModel.setRowCount(0);
            
            // Populate table with search results
            for (Item item : items) {
                Category category = categoryDAO.getCategoryById(item.getCategoryId());
                Supplier supplier = supplierDAO.getSupplierById(item.getSupplierId());
                
                String status = "In Stock";
                if (item.isOutOfStock()) {
                    status = "Out of Stock";
                } else if (item.isLowStock()) {
                    status = "Low Stock";
                }
                
                tableModel.addRow(new Object[] {
                        item.getItemId(),
                        item.getItemName(),
                        category != null ? category.getCategoryName() : "Unknown",
                        item.getQuantity(),
                        supplier != null ? supplier.getSupplierName() : "Unknown",
                        item.getUnitPrice(),
                        item.getReorderLevel(),
                        status
                });
            }
        }
    }
    
    private void filterItems() {
        String selectedFilter = (String) filterComboBox.getSelectedItem();
        
        List<Item> items;
        
        switch (selectedFilter) {
            case "Low Stock Items":
                items = itemDAO.getLowStockItems();
                break;
            case "Out of Stock Items":
                items = itemDAO.getOutOfStockItems();
                break;
            default: // "All Items"
                items = itemDAO.getAllItems();
                break;
        }
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Populate table with filtered results
        for (Item item : items) {
            Category category = categoryDAO.getCategoryById(item.getCategoryId());
            Supplier supplier = supplierDAO.getSupplierById(item.getSupplierId());
            
            String status = "In Stock";
            if (item.isOutOfStock()) {
                status = "Out of Stock";
            } else if (item.isLowStock()) {
                status = "Low Stock";
            }
            
            tableModel.addRow(new Object[] {
                    item.getItemId(),
                    item.getItemName(),
                    category != null ? category.getCategoryName() : "Unknown",
                    item.getQuantity(),
                    supplier != null ? supplier.getSupplierName() : "Unknown",
                    item.getUnitPrice(),
                    item.getReorderLevel(),
                    status
            });
        }
    }
    
    // Method to refresh data (can be called from outside)
    public void refreshData() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadItemData();
                loadCategories();
                loadSuppliers();
            }
        });
    }
} 