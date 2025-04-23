package auth;

import dao.UserDAO;
import model.User;

public class AuthManager {
    private static User currentUser = null;
    
    public static boolean login(String username, String password) {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            currentUser = user;
            return true;
        }
        
        return false;
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static boolean isAdmin() {
        return isLoggedIn() && currentUser.isAdmin();
    }
} 