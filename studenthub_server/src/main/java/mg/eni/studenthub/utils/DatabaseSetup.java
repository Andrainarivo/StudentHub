package mg.eni.studenthub.utils;

import java.sql.*;

public class DatabaseSetup {

    public static void initializeDatabase() {
        if (DB_Connection.isDbAvailable()) {
            try(Connection conn = DB_Connection.getConnection();
                Statement stmt = conn.createStatement()) {

                String createTableSQL = "CREATE TABLE IF NOT EXISTS students ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "regnum INT NOT NULL UNIQUE, "
                        + "firstname VARCHAR(100), "
                        + "lastname VARCHAR(100) NOT NULL, "
                        + "email VARCHAR(100) NOT NULL, "
                        + "id_card VARCHAR(100) NOT NULL UNIQUE, "
                        + "address VARCHAR(100), "
                        + "level VARCHAR(20) NOT NULL, "
                        + "scholarship VARCHAR(100))";
                stmt.executeUpdate(createTableSQL);

                String insertDataSQL = "INSERT INTO students (regnum, firstname, lastname, email, id_card, address, level, scholarship) VALUES "
                        + "('1001', 'John', 'Doe', 'john.doe@example.com', '301356978401', '123 Main St', 'M2', '2000000'), "
                        + "('1002', 'Jane', 'Smith', 'jane.smith@example.com', '401456123789', '456 Main Street', 'L3', '1000000')";
                stmt.executeUpdate(insertDataSQL);
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    public static void initTableUsers() {
        if (DB_Connection.isDbAvailable()) {
            try(Connection conn = DB_Connection.getConnection();
                Statement stmt = conn.createStatement()) {

                String createTableUser = "CREATE TABLE IF NOT EXISTS users ("
                        + "id  INT AUTO_INCREMENT PRIMARY KEY, "
                        + "username VARCHAR(50) NOT NULL UNIQUE, "
                        + "password_hash VARCHAR(255) NOT NULL)";
                stmt.executeUpdate(createTableUser);
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        } else {
            return;
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
        initTableUsers();
    }
    

}
