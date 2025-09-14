package mg.eni.studenthub.controller;

import java.util.logging.Logger;

import mg.eni.studenthub.client.StudentClient;
import mg.eni.studenthub.model.Student;
import mg.eni.studenthub.shared.StudentRequest;
import mg.eni.studenthub.shared.StudentResponse;
import mg.eni.studenthub.shared.StudentRequest.Action;

public class StudentService {

    private static final Logger LOGGER = Logger.getLogger(StudentService.class.getName());

    // 🔹 Méthode utilitaire centrale
    private static StudentResponse send(Action action, Object payload, Student student) {
        StudentRequest request;

        switch (action) {
            case CREATE:
                request = new StudentRequest(action, student);
                break;
            case READ_BY_ID, READ_BY_REGNUM, DELETE_BY_ID, DELETE_BY_REGNUM:
                request = new StudentRequest(action, (int) payload);
                break;
            case READ_BY_IDCARD:
                request = new StudentRequest(action, (String) payload);
                break;
            case UPDATE_BY_ID, UPDATE_BY_REGNUM:
                request = new StudentRequest(action, (int) payload, student);
                break;
            case READ_ALL:
                request = new StudentRequest(action);
                break;
            default:
                LOGGER.warning("Unsupported action: " + action);
                return new StudentResponse(false, "Unsupported action: " + action);
        }

        return StudentClient.sendRequest(request);
    }

    // 🔹 Méthodes publiques
    public static StudentResponse createStudent(Student student) {
        return send(Action.CREATE, null, student);
    }

    public static StudentResponse readStudentById(int id) {
        return send(Action.READ_BY_ID, id, null);
    }

    public static StudentResponse readStudentByRegNum(int regNum) {
        return send(Action.READ_BY_REGNUM, regNum, null);
    }

    public static StudentResponse readStudentByIdCard(String idCard) {
        return send(Action.READ_BY_IDCARD, idCard, null);
    }

    public static StudentResponse updateStudentById(int id, Student updatedStudent) {
        return send(Action.UPDATE_BY_ID, id, updatedStudent);
    }

    public static StudentResponse updateStudentByRegNum(int regnum, Student updatedStudent) {
        return send(Action.UPDATE_BY_REGNUM, regnum, updatedStudent);
    }

    public static StudentResponse deleteStudentById(int id) {
        return send(Action.DELETE_BY_ID, id, null);
    }

    public static StudentResponse deleteStudentByRegNum(int regnum) {
        return send(Action.DELETE_BY_REGNUM, regnum, null);
    }

    public static StudentResponse readAllStudents() {
        return send(Action.READ_ALL, null, null);
    }
}
