package mg.eni.studenthub.shared;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import mg.eni.studenthub.model.Student;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudentResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean state; // échec ou succés
    @XmlElement
    private String message;
    @XmlElement
    private Student student;
    @XmlElement(name = "student")
    private List<Student> students;

    // constructeurs
    public StudentResponse() {}

    public StudentResponse(boolean state, String message) {
        this.state = state;
        this.message = message;
    }

    public StudentResponse(boolean state, String message, Student student) {
        this.state = state;
        this.message = message;
        this.student = student;
    }

    public StudentResponse(boolean state, String message, List<Student> students) {
        this.state = state;
        this.message = message;
        this.students = students;
    }

    // getters && setters
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

    public void setStudent(Student Student) {
        this.student = Student;
    }

    public List<Student> getStudentList() {
        return students;
    }

    public void setStudentList(List<Student> studentList) {
        this.students = studentList;
    } 

}
