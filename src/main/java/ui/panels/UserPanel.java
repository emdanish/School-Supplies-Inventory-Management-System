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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import auth.AuthManager;
import dao.UserDAO;
import model.User;

public class UserPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JTable userTable;
    private DefaultTableModel tableModel;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullnameField;
    private JComboBox<String> roleComboBox;
    
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    private UserDAO userDAO;
    private User selectedUser;
    
    public UserPanel() {
        setLayout(new BorderLayout(10, 10));
        
        userDAO = new UserDAO();
        
        initComponents();
        loadUserData();
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
        tableModel.addColumn("Username");
        tableModel.addColumn("Full Name");
        tableModel.addColumn("Role");
        
        // Create JTable with the model
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        populateFormFields(selectedRow);
                    }
                }
            }
        });
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Full Name field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        fullnameField = new JTextField(20);
        formPanel.add(fullnameField, gbc);
        
        // Role combo box
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        roleComboBox = new JComboBox<>(new String[] {"ADMIN", "STAFF"});
        formPanel.add(roleComboBox, gbc);
        
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
                addUser();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUser();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
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
    
    private void loadUserData() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all users
        List<User> users = userDAO.getAllUsers();
        
        // Populate table with user data
        for (User user : users) {
            tableModel.addRow(new Object[] {
                    user.getUserId(),
                    user.getUsername(),
                    user.getFullname(),
                    user.getRole()
            });
        }
    }
    
    private void populateFormFields(int selectedRow) {
        // Get user ID from the selected row
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Get the corresponding User object
        selectedUser = userDAO.getUserById(userId);
        
        // Populate form fields
        if (selectedUser != null) {
            usernameField.setText(selectedUser.getUsername());
            passwordField.setText(selectedUser.getPassword());
            fullnameField.setText(selectedUser.getFullname());
            roleComboBox.setSelectedItem(selectedUser.getRole());
            
            // Don't allow admin to delete themselves
            boolean isSelf = AuthManager.getCurrentUser().getUserId() == selectedUser.getUserId();
            deleteButton.setEnabled(!isSelf);
            
            // Enable update button
            updateButton.setEnabled(true);
        }
    }
    
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        fullnameField.setText("");
        if (roleComboBox.getItemCount() > 0) {
            roleComboBox.setSelectedIndex(0);
        }
        
        selectedUser = null;
        userTable.clearSelection();
        
        // Disable update and delete buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private void addUser() {
        // Validate form fields
        if (!validateForm()) {
            return;
        }
        
        // Get values from form fields
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String fullname = fullnameField.getText();
        String role = (String) roleComboBox.getSelectedItem();
        
        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        
        // Create new User object
        User user = new User(username, password, fullname, role);
        
        // Add user to database
        if (userDAO.addUser(user)) {
            JOptionPane.showMessageDialog(this, "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload user data
            loadUserData();
            
            // Clear form
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUser() {
        // Check if a user is selected
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "No user selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate form fields
        if (!validateForm()) {
            return;
        }
        
        // Get values from form fields
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String fullname = fullnameField.getText();
        String role = (String) roleComboBox.getSelectedItem();
        
        // Check if username already exists and it's not the current user
        User existingUser = userDAO.getUserByUsername(username);
        if (existingUser != null && existingUser.getUserId() != selectedUser.getUserId()) {
            JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        
        // Don't allow changing the last admin to staff
        if ("STAFF".equals(role) && "ADMIN".equals(selectedUser.getRole())) {
            // Count how many admins exist
            int adminCount = 0;
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                if ("ADMIN".equals(user.getRole())) {
                    adminCount++;
                }
            }
            
            if (adminCount <= 1) {
                JOptionPane.showMessageDialog(this, 
                        "Cannot change the last admin to staff", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Update the User object
        selectedUser.setUsername(username);
        selectedUser.setPassword(password);
        selectedUser.setFullname(fullname);
        selectedUser.setRole(role);
        
        // Update user in database
        if (userDAO.updateUser(selectedUser)) {
            JOptionPane.showMessageDialog(this, "User updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // If the current user is updated, update the session
            if (AuthManager.getCurrentUser().getUserId() == selectedUser.getUserId()) {
                // Re-authenticate
                AuthManager.login(username, password);
            }
            
            // Reload user data
            loadUserData();
            
            // Clear form
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteUser() {
        // Check if a user is selected
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "No user selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Don't allow deleting the current user
        if (AuthManager.getCurrentUser().getUserId() == selectedUser.getUserId()) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Don't allow deleting the last admin
        if ("ADMIN".equals(selectedUser.getRole())) {
            // Count how many admins exist
            int adminCount = 0;
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                if ("ADMIN".equals(user.getRole())) {
                    adminCount++;
                }
            }
            
            if (adminCount <= 1) {
                JOptionPane.showMessageDialog(this, 
                        "Cannot delete the last admin", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Confirm deletion
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this user?\nThis action cannot be undone.", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Delete user from database
            if (userDAO.deleteUser(selectedUser.getUserId())) {
                JOptionPane.showMessageDialog(this, "User deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reload user data
                loadUserData();
                
                // Clear form
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateForm() {
        // Validate username
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username", "Validation Error", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return false;
        }
        
        // Validate password
        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Please enter password", "Validation Error", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return false;
        }
        
        // Validate full name
        if (fullnameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter full name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            fullnameField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    // Method to refresh data (can be called from outside)
    public void refreshData() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadUserData();
            }
        });
    }
} 