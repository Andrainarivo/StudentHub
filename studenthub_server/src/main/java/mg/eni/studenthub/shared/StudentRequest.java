package mg.eni.studenthub.shared;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import mg.eni.studenthub.model.Student;

@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    // type d'action d'un request
    public enum Action {
        CREATE, READ_BY_ID, READ_BY_REGNUM, READ_BY_IDCARD, READ_ALL, UPDATE_BY_ID, UPDATE_BY_REGNUM, DELETE
    }

    @XmlElement
    private Action action;
    @XmlElement
    private int num; // id or regnum
    @XmlElement
    private Student student;
    @XmlElement
    private String idcard;

    // Constructeurs

    public StudentRequest() {}

    // pour la requete CREATE
    public StudentRequest(Action action, Student student) {
        this.action = action;
        this.student = student;
        this.num = student.get_regNum();
        this.idcard = student.get_idCard();
    }

    // pour la requete readAll
    public StudentRequest(Action action) {
        this.action = action;
    }

    // pour les requetes findBy(ID/RegNum), delete(regnum/id)
    public StudentRequest(Action action, int num) {
        this.action = action;
        this.num = num;
    }

    // Pour la requete findByIdCard
    public StudentRequest(Action action, String idcard) {
        this.action = action;
        this.idcard = idcard;
    }

    // pour les requetes : updateByRegNum/id,
    public StudentRequest(Action action, int nb, Student student) {
        this.action = action;
        this.num = nb;
        this.student = student;
    }



    // getters & setters
    //@XmlElement
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    //@XmlElement
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    //@XmlElement
    public int getNumber() {
        return num;
    }

    public void setNumber(int nb) {
        this.num = nb;
    }

    //@XmlElement
    public String get_idCard() {
        return idcard;
    }

    public void setIdCard(String idcard) {
        this.idcard = idcard;
    }

}
