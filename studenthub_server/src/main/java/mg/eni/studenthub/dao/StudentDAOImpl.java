package mg.eni.studenthub.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import mg.eni.studenthub.config.LoggerConfig;
import mg.eni.studenthub.model.Student;
import mg.eni.studenthub.utils.DB_Connection;

public class StudentDAOImpl implements StudentDAO {

    private static final Logger LOGGER = Logger.getLogger(StudentDAOImpl.class.getName());

    private Student createStudentFromResultSet(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getInt("regnum"),
            rs.getString("firstname"),
            rs.getString("lastname"),
            rs.getString("email"),
            rs.getString("address"),
            rs.getString("level"),
            rs.getString("id_card"),
            rs.getString("scholarship")
        );
    }

    @Override
    public Student findByID(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";

        try(Connection conn = DB_Connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return createStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
            //LOGGER.log(Level.WARNING, e.getMessage());
        }
        return null;
    }
    
    @Override
    public Student findByRegNum(int reg_num) {
        //LoggerConfig.setup();
        String sql = "SELECT * FROM students WHERE regnum = ?";

        try(Connection conn = DB_Connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, reg_num);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return createStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
            //LOGGER.log(Level.WARNING, e.getMessage());
        }
        return null;
    }

    @Override
    public Student findByIdCard(String id_card) {
        String sql = "SELECT * FROM students WHERE id_card = ?";

        try(Connection conn = DB_Connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id_card);;
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return createStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            //LOGGER.warning(e.getMessage());
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        return null;

    }

    @Override
    public boolean insertStudent(Student student) {
        String sql = "INSERT INTO students (regnum, firstname, lastname, email, id_card, address, level, scholarship) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DB_Connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, student.get_regNum());
            ps.setString(2, student.get_fname());
            ps.setString(3, student.get_lname());
            ps.setString(4, student.get_email());
            ps.setString(5, student.get_idCard());
            ps.setString(6, student.get_address());
            ps.setString(7, student.get_level());
            ps.setString(8, student.get_scholarship());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(int reg_num) {
        String sql = "DELETE FROM students WHERE regnum = ?";

        try(Connection conn = DB_Connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, reg_num);
            return ps.executeUpdate() > 0 ;
            
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean updateStudentById(int id, Student student) {
        String sql = "UPDATE students SET regnum = ?, firstname = ?, lastname = ?, email = ?, id_card = ?, address = ?, level = ?, scholarship = ? WHERE id = ?";
        try(Connection conn = DB_Connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, student.get_regNum());
            ps.setString(2, student.get_fname());
            ps.setString(3, student.get_lname());
            ps.setString(4, student.get_email());
            ps.setString(5, student.get_idCard());
            ps.setString(6, student.get_address());
            ps.setString(7, student.get_level());
            ps.setString(8, student.get_scholarship());
            ps.setInt(9, id);

            return ps.executeUpdate() > 0;
            
         } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            return false;
         }
    }

    @Override
    public boolean updateStudentByRegNum(int reg_num, Student student) {
        String sql = "UPDATE students SET firstname= ?, lastname= ?, email=?, id_card=?, address=?, level= ?, scholarship= ? WHERE regnum = ?";

        try(Connection conn = DB_Connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, student.get_fname());
            ps.setString(2, student.get_lname());
            ps.setString(3, student.get_email());
            ps.setString(4, student.get_idCard());
            ps.setString(5, student.get_address());
            ps.setString(6, student.get_level());
            ps.setString(7, student.get_scholarship());
            ps.setInt(8, reg_num);

            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            return false;
        }
    }
        
    @Override
    public void batchInsertStudents(List<Student> students) {
        LoggerConfig.setup();
        String sql = "INSERT INTO students (regnum, firstname, lastname, email, id_card, address, level, scholarship) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DB_Connection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Disable auto-commit
            conn.setAutoCommit(false);

            for (Student student : students) {
                ps.setInt(1, student.get_regNum());
                ps.setString(2, student.get_fname());
                ps.setString(3, student.get_lname());
                ps.setString(4, student.get_email());
                ps.setString(5, student.get_idCard());
                ps.setString(6, student.get_address());
                ps.setString(7, student.get_level());
                ps.setString(8, student.get_scholarship());
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            conn.commit();
            LOGGER.log(Level.INFO, "Batch executed successfully. Inserted {0} records.", results.length);;
           
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing batch", e);
            try (Connection connection = DB_Connection.getConnection()) {
                if (connection != null) {
                    connection.rollback();
                    LOGGER.log(Level.INFO, "Transaction rolled back successfully.");
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex);
            }
        }
        
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        
        try(Connection conn = DB_Connection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(createStudentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }

        return students;
    }

    

}
