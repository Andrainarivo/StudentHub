package mg.eni.studenthub.utils;

import java.sql.*;

import mg.eni.studenthub.config.ConfigLoader;

public class DB_Connection {

    private static final String url = ConfigLoader.get("db.url");;
    private static final String user = ConfigLoader.get("db.user");
    private static final String password = ConfigLoader.get("db.password");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean isDbAvailable() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();

        } catch (SQLException e) {
            return false;
        }
    }
}
