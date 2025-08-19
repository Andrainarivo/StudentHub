package mg.eni.studenthub.dao;

import java.util.List;

import mg.eni.studenthub.model.Student;

public interface StudentDAO {
    Student findByID(int id);
    Student findByRegNum(int reg_num);
    Student findByIdCard(String id_card);
    boolean insertStudent(Student student);
    boolean updateStudentById(int id, Student student);
    boolean updateStudentByRegNum(int reg_num, Student student);
    boolean delete(int reg_num);
    void batchInsertStudents(List<Student> students);
    List<Student> getAllStudents();


}
