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
import model.Category;

public class CategoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    private CategoryDAO categoryDAO;
    private ItemDAO itemDAO;
    
    private Category selectedCategory;
    
    public CategoryPanel() {
        setLayout(new BorderLayout(10, 10));
        
        categoryDAO = new CategoryDAO();
        itemDAO = new ItemDAO();
        
        initComponents();
        loadCategoryData();
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
        tableModel.addColumn("Category Name");
        tableModel.addColumn("Items Count");
        
        // Create JTable with the model
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = categoryTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        populateFormFields(selectedRow);
                    }
                }
            }
        });
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        
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
        formPanel.add(new JLabel("Category Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCategory();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCategory();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCategory();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        // Initially disable update and delete buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);
        
        // Create input panel to hold form and buttons
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(formPanel, BorderLayout.CENTER);
        inputPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Add components to the main panel
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void loadCategoryData() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all categories
        List<Category> categories = categoryDAO.getAllCategories();
        
        // Populate table with category data
        for (Category category : categories) {
            List<model.Item> items = itemDAO.getItemsByCategory(category.getCategoryId());
            tableModel.addRow(new Object[] {
                    category.getCategoryId(),
                    category.getCategoryName(),
                    items.size()
            });
        }
    }
    
    private void populateFormFields(int selectedRow) {
        // Get category ID from the selected row
        int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Get the corresponding Category object
        selectedCategory = categoryDAO.getCategoryById(categoryId);
        
        // Populate form fields
        if (selectedCategory != null) {
            nameField.setText(selectedCategory.getCategoryName());
            
            // Enable update and delete buttons
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        
        selectedCategory = null;
        categoryTable.clearSelection();
        
        // Disable update and delete buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private void addCategory() {
        // Validate form fields
        if (!validateForm()) {
            return;
        }
        
        // Get values from form fields
        String name = nameField.getText();
        
        // Create new Category object
        Category category = new Category(name);
        
        // Add category to database
        if (categoryDAO.addCategory(category)) {
            JOptionPane.showMessageDialog(this, "Category added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload category data
            loadCategoryData();
            
            // Clear form
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add category", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCategory() {
        // Check if a category is selected
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "No category selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate form fields
        if (!validateForm()) {
            return;
        }
        
        // Get values from form fields
        String name = nameField.getText();
        
        // Update the Category object
        selectedCategory.setCategoryName(name);
        
        // Update category in database
        if (categoryDAO.updateCategory(selectedCategory)) {
            JOptionPane.showMessageDialog(this, "Category updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload category data
            loadCategoryData();
            
            // Clear form
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update category", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCategory() {
        // Check if a category is selected
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "No category selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if there are items in this category
        List<model.Item> items = itemDAO.getItemsByCategory(selectedCategory.getCategoryId());
        if (!items.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Cannot delete this category because it contains " + items.size() + " item(s).\n" +
                    "Please reassign or delete these items first.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this category?\nThis action cannot be undone.", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Delete category from database
            if (categoryDAO.deleteCategory(selectedCategory.getCategoryId())) {
                JOptionPane.showMessageDialog(this, "Category deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reload category data
                loadCategoryData();
                
                // Clear form
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateForm() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter category name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    // Method to refresh data (can be called from outside)
    public void refreshData() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadCategoryData();
            }
        });
    }
} 