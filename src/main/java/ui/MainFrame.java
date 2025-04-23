package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import auth.AuthManager;
import config.DatabaseConfig;
import model.User;
import ui.panels.CategoryPanel;
import ui.panels.InventoryPanel;
import ui.panels.ReportPanel;
import ui.panels.SupplierPanel;
import ui.panels.TransactionPanel;
import ui.panels.UserPanel;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JTabbedPane tabbedPane;
    private InventoryPanel inventoryPanel;
    private CategoryPanel categoryPanel;
    private SupplierPanel supplierPanel;
    private TransactionPanel transactionPanel;
    private ReportPanel reportPanel;
    private UserPanel userPanel;
    
    public MainFrame() {
        setTitle("School Supplies Inventory Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Handle window closing event to clean up resources
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        
        initComponents();
    }
    
    private void initComponents() {
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        fileMenu.add(exitMenuItem);
        
        // Account menu
        JMenu accountMenu = new JMenu("Account");
        JMenuItem logoutMenuItem = new JMenuItem("Logout");
        logoutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        accountMenu.add(logoutMenuItem);
        
        menuBar.add(fileMenu);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create panels
        inventoryPanel = new InventoryPanel();
        categoryPanel = new CategoryPanel();
        supplierPanel = new SupplierPanel();
        transactionPanel = new TransactionPanel();
        reportPanel = new ReportPanel();
        
        // Add tabs
        tabbedPane.addTab("Inventory", inventoryPanel);
        tabbedPane.addTab("Categories", categoryPanel);
        tabbedPane.addTab("Suppliers", supplierPanel);
        tabbedPane.addTab("Transactions", transactionPanel);
        tabbedPane.addTab("Reports", reportPanel);
        
        // Add user management tab for admin users only
        User currentUser = AuthManager.getCurrentUser();
        if (currentUser != null && currentUser.isAdmin()) {
            userPanel = new UserPanel();
            tabbedPane.addTab("User Management", userPanel);
        }
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            AuthManager.logout();
            
            // Show login window
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                }
            });
            
            // Close current window
            dispose();
        }
    }
    
    private void exit() {
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // No need to close connection here as we're creating new connections each time
            // and they are auto-closed with try-with-resources
            
            // Exit application
            System.exit(0);
        }
    }
} 