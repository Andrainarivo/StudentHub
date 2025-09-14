package mg.eni.studenthub.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.sql.*;

public class UserManager {

    // Ajouter un utilisateur
    public static void addUser(String username, String plainPassword) {
        String hash = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());

        if (DB_Connection.isDbAvailable()) {
            String sql = "INSERT INTO users(username, password_hash) VALUES (?, ?)";
            try (Connection conn = DB_Connection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, hash);
                stmt.executeUpdate();
                System.out.println("✅ User ajouté en base: " + username);
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Supprimer un utilisateur
    public static void removeUser(String username) {
        if (DB_Connection.isDbAvailable()) {
            String sql = "DELETE FROM users WHERE username = ?";
            try (Connection conn = DB_Connection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
                System.out.println("🗑️ User supprimé de la base: " + username);
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        addUser("admin", "Admin123!");
        
    }
}

