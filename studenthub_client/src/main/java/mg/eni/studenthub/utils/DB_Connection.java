package mg.eni.studenthub.utils;

import java.sql.*;
//import java.util.logging.Logger;

import mg.eni.studenthub.config.ConfigLoader;
//import mg.eni.studenthub.config.LoggerConfig;

public class DB_Connection {
    
    //private static final Logger LOGGER = Logger.getLogger(DB_Connection.class.getName());

    private static final String url = ConfigLoader.get("db.url");;
    private static final String user = ConfigLoader.get("db.user");
    private static final String password = ConfigLoader.get("db.password");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean isDbAvailable() {
        //LoggerConfig.setup();
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();

        } catch (SQLException e) {
            //LOGGER.warning("❌ Base de données MySQL indisponible : " + e.getMessage());
            return false;
        }
    }

}
