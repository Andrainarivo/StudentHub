package mg.eni.studenthub.client;

import mg.eni.studenthub.model.Student;
import mg.eni.studenthub.shared.StudentRequest;
import mg.eni.studenthub.shared.StudentResponse;
import mg.eni.studenthub.shared.StudentRequest.Action;

public class StudentService {
    public static StudentResponse createStudent(Student student) {
        StudentRequest request = new StudentRequest(Action.CREATE, student);
        return StudentClient.sendRequest(request);
    }

    public static StudentResponse readStudentByRegNum(int regNum) {
        StudentRequest request = new StudentRequest(Action.READ_BY_REGNUM, regNum);
        return StudentClient.sendRequest(request);
    }

    public static StudentResponse readStudentByIdCard(String idCard) {
        StudentRequest request = new StudentRequest(Action.READ_BY_IDCARD, idCard);
        return StudentClient.sendRequest(request);
    }

    public static StudentResponse updateStudent(int id, Student updatedStudent) {
        StudentRequest request = new StudentRequest(Action.UPDATE_BY_ID, id, updatedStudent);
        return StudentClient.sendRequest(request);
    }

    public static StudentResponse deleteStudent(int regnum) {
        StudentRequest request = new StudentRequest(Action.DELETE, regnum);
        return StudentClient.sendRequest(request);
    }

    public static StudentResponse readAllStudents() {
        StudentRequest request = new StudentRequest(Action.READ_ALL);
        return StudentClient.sendRequest(request);
    }
}
