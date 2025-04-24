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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import dao.ItemDAO;
import dao.SupplierDAO;
import model.Supplier;

public class SupplierPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField contactPersonField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;
    
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    private SupplierDAO supplierDAO;
    private ItemDAO itemDAO;
    
    private Supplier selectedSupplier;
    
    public SupplierPanel() {
        setLayout(new BorderLayout(10, 10));
        
        supplierDAO = new SupplierDAO();
        itemDAO = new ItemDAO();
        
        initComponents();
        loadSupplierData();
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
        tableModel.addColumn("Supplier Name");
        tableModel.addColumn("Contact Person");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Email");
        tableModel.addColumn("Items Count");
        
        // Create JTable with the model
        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = supplierTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        populateFormFields(selectedRow);
                    }
                }
            }
        });
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        
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
        formPanel.add(new JLabel("Supplier Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Contact Person field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Contact Person:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contactPersonField = new JTextField(20);
        formPanel.add(contactPersonField, gbc);
        
        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Phone:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);
        
        // Email field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Address area
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        formPanel.add(addressScrollPane, gbc);
        
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
                addSupplier();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSupplier();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSupplier();
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
    
    private void loadSupplierData() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all suppliers
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        
        // Populate table with supplier data
        for (Supplier supplier : suppliers) {
            List<model.Item> items = itemDAO.getItemsBySupplier(supplier.getSupplierId());
            tableModel.addRow(new Object[] {
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getContactPerson(),
                    supplier.getPhone(),
                    supplier.getEmail(),
                    items.size()
            });
        }
    }
    
    private void populateFormFields(int selectedRow) {
        // Get supplier ID from the selected row
        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Get the corresponding Supplier object
        selectedSupplier = supplierDAO.getSupplierById(supplierId);
        
        // Populate form fields
        if (selectedSupplier != null) {
            nameField.setText(selectedSupplier.getSupplierName());
            contactPersonField.setText(selectedSupplier.getContactPerson());
            phoneField.setText(selectedSupplier.getPhone());
            emailField.setText(selectedSupplier.getEmail());
            addressArea.setText(selectedSupplier.getAddress());
            
            // Enable update and delete buttons
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        contactPersonField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        
        selectedSupplier = null;
        supplierTable.clearSelection();
        
        // Disable update and delete buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private void addSupplier() {
        // Validate form fields
        if (!validateForm()) {
            return;
        }
        
        // Get values from form fields
        String name = nameField.getText();
        String contactPerson = contactPersonField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String address = addressArea.getText();
        
        // Create new Supplier object
        Supplier supplier = new Supplier(name, contactPerson, phone, email, address);
        
        // Add supplier to database
        if (supplierDAO.addSupplier(supplier)) {
            JOptionPane.showMessageDialog(this, "Supplier added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload supplier data
            loadSupplierData();
            
            // Clear form
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add supplier", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateSupplier() {
        // Check if a supplier is selected
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, "No supplier selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate form fields
        if (!validateForm()) {
            return;
        }
        
        // Get values from form fields
        String name = nameField.getText();
        String contactPerson = contactPersonField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String address = addressArea.getText();
        
        // Update the Supplier object
        selectedSupplier.setSupplierName(name);
        selectedSupplier.setContactPerson(contactPerson);
        selectedSupplier.setPhone(phone);
        selectedSupplier.setEmail(email);
        selectedSupplier.setAddress(address);
        
        // Update supplier in database
        if (supplierDAO.updateSupplier(selectedSupplier)) {
            JOptionPane.showMessageDialog(this, "Supplier updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload supplier data
            loadSupplierData();
            
            // Clear form
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update supplier", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSupplier() {
        // Check if a supplier is selected
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, "No supplier selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if supplier has associated items
        List<model.Item> items = itemDAO.getItemsBySupplier(selectedSupplier.getSupplierId());
        if (!items.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete supplier with associated items. Please reassign or delete the items first.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this supplier?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete supplier from database
            if (supplierDAO.deleteSupplier(selectedSupplier.getSupplierId())) {
                JOptionPane.showMessageDialog(this, "Supplier deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reload supplier data
                loadSupplierData();
                
                // Clear form
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete supplier", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateForm() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter supplier name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        // Validate contact person
        if (contactPersonField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter contact person", "Validation Error", JOptionPane.ERROR_MESSAGE);
            contactPersonField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    // Method to refresh data (can be called from outside)
    public void refreshData() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadSupplierData();
            }
        });
    }
} 