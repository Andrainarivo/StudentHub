package mg.eni.studenthub.shared;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import mg.eni.studenthub.model.Student;

/**
 * Objet représentant la réponse du serveur à une requête {@link StudentRequest}.
 * Peut contenir un étudiant unique, une liste d’étudiants, ou simplement un état avec un message.
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudentResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean state;              // true = succès, false = échec
    @XmlElement private String message; // message associé à la réponse (info ou erreur)
    @XmlElement private Student student; 
    @XmlElement(name = "student") private List<Student> students;

    // --- Constructeurs ---

    /** Réponse simple (succès/échec + message) */
    public StudentResponse() {}

    public StudentResponse(boolean state, String message) {
        this.state = state;
        this.message = message;
    }

    /** Réponse avec un étudiant unique */
    public StudentResponse(boolean state, String message, Student student) {
        this.state = state;
        this.message = message;
        this.student = student;
    }

    /** Réponse avec une liste d’étudiants */
    public StudentResponse(boolean state, String message, List<Student> students) {
        this.state = state;
        this.message = message;
        this.students = students;
    }

    // --- Getters & Setters ---

    public boolean isSuccess() {
        return state;
    }
    public void setSuccess(boolean state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }

    public List<Student> getStudentList() {
        return students;
    }
    public void setStudentList(List<Student> studentList) {
        this.students = studentList;
    }
}
