package mg.eni.studenthub.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


@XmlRootElement(name = "Student")
public class Student implements Serializable {

    private int id;
    private int reg_num; // registration number (matriculation)
    private String first_name;
    private String last_name;
    private String email;
    private String address;
    private String scholarship; // bourse
    private String id_card; // carte d'identité
    private String level;

    public Student() {}

    public Student(int reg_num, String fname, String lname, String email, String address, String level, String id_card ,String scholarship) {
        this.reg_num = reg_num;
        this.first_name = fname;
        this.last_name = lname;
        this.email = email;
        this.address = address;
        this.level = level;
        this.id_card = id_card;
        this.scholarship = scholarship;
    }

    public Student(int id, int reg_num, String fname, String lname, String email, String address, String level, String id_card ,String scholarship) {
        this.id = id;
        this.reg_num = reg_num;
        this.first_name = fname;
        this.last_name = lname;
        this.email = email;
        this.address = address;
        this.level = level;
        this.id_card = id_card;
        this.scholarship = scholarship;
    }

    @XmlElement
    public int getID() {
        return id;
    }
    

    @XmlElement
    public int get_regNum() { return reg_num;}

    public void set_regNum(int reg_num) {
        this.reg_num = reg_num;
    }

    @XmlElement
    public String get_idCard () {
        return id_card;
    }

    public void set_IdCard(String id_card) {
        this.id_card = id_card;
    }

    @XmlElement
    public String get_fname() {
        return first_name;
    }
    public void set_fname(String fname) {
        this.first_name = fname;
    }

    @XmlElement
    public String get_lname() {
        return last_name;
    }
    public void set_lname(String lname) {
        this.last_name = lname;
    }

    @XmlElement
    public String get_email() {
        return email;
    }
    public void set_email(String email) {
        this.email = email;
    }

    @XmlElement
    public String get_address() {
        return address;
    }
    public void set_address(String address) {
        this.address = address;
    }

    @XmlElement
    public String get_level() {
        return level;
    }
    public void set_level(String level) {
        this.level = level;
    }

    @XmlElement
    public String get_scholarship() {
        return scholarship;
    }

    public void set_scholarship(String scholarship) {
        this.scholarship = scholarship;
    }

    @Override
    public String toString() {
        return "ID : " + id + '\\' + "numéro: " + reg_num + '\\' + " prénom: " + last_name + '\\' + " email: " + email + '\\' + " level: " + level + '\\' + " address:" + address + '\\' + " bourse: " + scholarship;
    }


}

