package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConfig;
import model.Transaction;

public class TransactionDAO {
    
    // Create a new transaction
    public boolean addTransaction(Transaction transaction) {
        String query = "INSERT INTO transactions (item_id, transaction_type, quantity) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, transaction.getItemId());
            stmt.setString(2, transaction.getType());
            stmt.setInt(3, transaction.getQuantity());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getInt(1));
                }
                
                // Update item quantity in inventory
                updateItemQuantity(transaction);
                
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get transaction by ID
    public Transaction getTransactionById(int transactionId) {
        String query = "SELECT t.*, i.item_name FROM transactions t " +
                      "JOIN items i ON t.item_id = i.item_id " +
                      "WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get all transactions
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.*, i.item_name FROM transactions t " +
                      "JOIN items i ON t.item_id = i.item_id " +
                      "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    // Get transactions by item ID
    public List<Transaction> getTransactionsByItem(int itemId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.*, i.item_name FROM transactions t " +
                      "JOIN items i ON t.item_id = i.item_id " +
                      "WHERE t.item_id = ? " +
                      "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    // Helper method to extract transaction from result set
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setItemId(rs.getInt("item_id"));
        transaction.setItemName(rs.getString("item_name"));
        transaction.setType(rs.getString("transaction_type"));
        transaction.setQuantity(rs.getInt("quantity"));
        transaction.setDate(rs.getTimestamp("transaction_date"));
        return transaction;
    }
    
    // Helper method to update item quantity based on transaction
    private boolean updateItemQuantity(Transaction transaction) {
        // Get current item quantity
        String selectQuery = "SELECT quantity FROM items WHERE item_id = ?";
        String updateQuery = "UPDATE items SET quantity = ? WHERE item_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            
            selectStmt.setInt(1, transaction.getItemId());
            ResultSet rs = selectStmt.executeQuery();
            
            if (rs.next()) {
                int currentQuantity = rs.getInt("quantity");
                int newQuantity;
                
                if ("IN".equals(transaction.getType())) {
                    newQuantity = currentQuantity + transaction.getQuantity();
                } else { // "OUT"
                    newQuantity = currentQuantity - transaction.getQuantity();
                    // Prevent negative quantities
                    if (newQuantity < 0) {
                        newQuantity = 0;
                    }
                }
                
                updateStmt.setInt(1, newQuantity);
                updateStmt.setInt(2, transaction.getItemId());
                
                int rowsAffected = updateStmt.executeUpdate();
                return rowsAffected > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
} 