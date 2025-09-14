package mg.eni.studenthub.shared;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import mg.eni.studenthub.model.Student;

/**
 * Objet représentant une requête envoyée entre client et serveur
 * concernant la gestion des étudiants.
 * 
 * Sérialisable et utilisable en XML via JAXB.
 */
@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Enum listant toutes les actions possibles dans une requête.
     * (CRUD classique + recherche par différents critères)
     */
    public enum Action {
        CREATE,
        READ_BY_ID,
        READ_BY_REGNUM,
        READ_BY_IDCARD,
        READ_ALL,
        UPDATE_BY_ID,
        UPDATE_BY_REGNUM,
        DELETE_BY_ID,
        DELETE_BY_REGNUM
    }

    @XmlElement private Action action;    // Type d'action
    @XmlElement private int num;          // Identifiant numérique (id ou regnum)
    @XmlElement private Student student;  // Étudiant concerné (pour CREATE/UPDATE)
    @XmlElement private String idcard;    // Identifiant par carte (CIN)

    // --- Constructeurs ---

    public StudentRequest() {}

    /** Requête CREATE */
    public StudentRequest(Action action, Student student) {
        this.action = action;
        this.student = student;
        this.num = student.get_regNum();
        this.idcard = student.get_idCard();
    }

    /** Requête READ_ALL (pas besoin de paramètres) */
    public StudentRequest(Action action) {
        this.action = action;
    }

    /** Requêtes READ/DELETE par ID ou RegNum */
    public StudentRequest(Action action, int num) {
        this.action = action;
        this.num = num;
    }

    /** Requête READ par ID Card */
    public StudentRequest(Action action, String idcard) {
        this.action = action;
        this.idcard = idcard;
    }

    /** Requêtes UPDATE (par id ou regnum) */
    public StudentRequest(Action action, int nb, Student student) {
        this.action = action;
        this.num = nb;
        this.student = student;
    }

    // --- Getters & Setters ---

    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }

    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }

    public int getNumber() {
        return num;
    }
    public void setNumber(int nb) {
        this.num = nb;
    }

    public String get_idCard() {
        return idcard;
    }
    public void setIdCard(String idcard) {
        this.idcard = idcard;
    }
}
