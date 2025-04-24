package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConfig;
import model.Item;

public class ItemDAO {
    
    // Create a new item
    public boolean addItem(Item item) {
        String query = "INSERT INTO items (item_name, category_id, quantity, supplier_id, unit_price, minimum_quantity) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, item.getItemName());
            stmt.setInt(2, item.getCategoryId());
            stmt.setInt(3, item.getQuantity());
            stmt.setInt(4, item.getSupplierId());
            stmt.setDouble(5, item.getUnitPrice());
            stmt.setInt(6, item.getReorderLevel());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    item.setItemId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update an existing item
    public boolean updateItem(Item item) {
        String query = "UPDATE items SET item_name = ?, category_id = ?, quantity = ?, " +
                      "supplier_id = ?, unit_price = ?, minimum_quantity = ? WHERE item_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, item.getItemName());
            stmt.setInt(2, item.getCategoryId());
            stmt.setInt(3, item.getQuantity());
            stmt.setInt(4, item.getSupplierId());
            stmt.setDouble(5, item.getUnitPrice());
            stmt.setInt(6, item.getReorderLevel());
            stmt.setInt(7, item.getItemId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update item quantity only
    public boolean updateItemQuantity(int itemId, int newQuantity) {
        String query = "UPDATE items SET quantity = ? WHERE item_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, itemId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete an item
    public boolean deleteItem(int itemId) {
        Connection conn = null;
        try {
            // Get a connection and disable auto-commit for transaction
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // First, delete any related transactions
            String deleteTransactionsQuery = "DELETE FROM transactions WHERE item_id = ?";
            try (PreparedStatement deleteTransactionsStmt = conn.prepareStatement(deleteTransactionsQuery)) {
                deleteTransactionsStmt.setInt(1, itemId);
                deleteTransactionsStmt.executeUpdate();
            }
            
            // Now delete the item
            String deleteItemQuery = "DELETE FROM items WHERE item_id = ?";
            try (PreparedStatement deleteItemStmt = conn.prepareStatement(deleteItemQuery)) {
                deleteItemStmt.setInt(1, itemId);
                int rowsAffected = deleteItemStmt.executeUpdate();
                
                // Commit transaction
                conn.commit();
                return rowsAffected > 0;
            }
            
        } catch (SQLException e) {
            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Reset auto-commit and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
    
    // Get item by ID
    public Item getItemById(int itemId) {
        String query = "SELECT * FROM items WHERE item_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractItemFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get all items
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Search items by name
    public List<Item> searchItemsByName(String searchTerm) {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items WHERE item_name LIKE ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Get items by category
    public List<Item> getItemsByCategory(int categoryId) {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items WHERE category_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Get items by supplier
    public List<Item> getItemsBySupplier(int supplierId) {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items WHERE supplier_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, supplierId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Get low stock items
    public List<Item> getLowStockItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items WHERE quantity <= minimum_quantity AND quantity > 0";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Get out of stock items
    public List<Item> getOutOfStockItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items WHERE quantity <= 0";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Helper method to extract item from result set
    private Item extractItemFromResultSet(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getInt("item_id"));
        item.setItemName(rs.getString("item_name"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setSupplierId(rs.getInt("supplier_id"));
        item.setUnitPrice(rs.getDouble("unit_price"));
        item.setReorderLevel(rs.getInt("minimum_quantity"));
        return item;
    }
} 