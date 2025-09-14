package mg.eni.studenthub.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import mg.eni.studenthub.utils.DB_Connection;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserAuthService {
    private static final String USER_FILE = "users.json"; // offline persistence

    /**
     * Authentifie un utilisateur.
     * Mode 1: online → base MySQL
     * Mode 2: offline → JSON si DB et serveur java sont éteints
     */
    public static boolean authenticate(String username, String password) {
        // Tenter d'abord la DB
        if (DB_Connection.isDbAvailable()) {
            String hash = getUserHashFromDb(username);
            if (hash != null && BCrypt.verifyer().verify(password.toCharArray(), hash).verified) {
                saveUserToJson(username, hash); // synchro offline
                return true;
            }
        }

        // Sinon → fallback JSON
        return authenticateOffline(username, password);
    }

    /**
     * Récupère le hash depuis MySQL.
     */
    private static String getUserHashFromDb(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = DB_Connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Vérifie l’utilisateur en mode offline (fichier JSON).
     */
    private static boolean authenticateOffline(String username, String password) {
        File file = new File(USER_FILE);
        if (!file.exists()) return false;

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> users = mapper.readValue(file, Map.class);

            String hash = users.get(username);
            return hash != null && BCrypt.verifyer().verify(password.toCharArray(), hash).verified;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sauvegarde/synchro l’utilisateur validé dans le JSON offline.
     */
    private static void saveUserToJson(String username, String hash) {
        File file = new File(USER_FILE);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> users = new HashMap<>();

        try {
            if (file.exists()) {
                users = mapper.readValue(file, Map.class);
            }
            users.put(username, hash);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

