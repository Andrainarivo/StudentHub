package mg.eni.studenthub.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;

import mg.eni.studenthub.config.LoggerConfig;
import mg.eni.studenthub.dao.StudentDAOImpl;
import mg.eni.studenthub.model.Student;
import mg.eni.studenthub.shared.StudentRequest;
import mg.eni.studenthub.shared.StudentResponse;
import mg.eni.studenthub.shared.StudentRequest.Action;


public class StudentHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(StudentHandler.class.getName());
    private final SSLSocket sslSocket;
    private final StudentDAOImpl dao = new StudentDAOImpl();

    public StudentHandler(SSLSocket sslSocket) {
        this.sslSocket = sslSocket;
    }

    @Override
    public void run() {
        LoggerConfig.setup();
        System.out.println("✅ Requete du client : " + sslSocket.getInetAddress());
        try (
            ObjectOutputStream out = new ObjectOutputStream(sslSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(sslSocket.getInputStream());
        ) {
            // Lecture de la requete
            StudentRequest request = (StudentRequest) in.readObject();
            // Traitement de la requete
            StudentResponse response = handleRequest(request);

            // Envoi de la réponse
            out.writeObject(response);
            out.flush();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            try {
                sslSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "❌ Erreur à la fermeture du socket : ", e.getMessage());
            }
        }
    }

    private StudentResponse handleRequest(StudentRequest request) {
        Action action = request.getAction();

        switch (action) {
            case CREATE:
                boolean created = dao.insertStudent(request.getStudent());
                return new StudentResponse(created, created ? "✅ Étudiant ajouté avec succès" : "❌ Échec de l'ajout");

            case READ_BY_ID:
                Student student1 = dao.findByID(request.getNumber());

                if (student1 != null) {
                    StudentResponse response = new StudentResponse(true, "Etudiant avec l'identifiant : "+ request.getNumber(), student1);
                    return response;
                } else {
                    return new StudentResponse(false, "Aucun étudiant avec ce numéro de matriculation");
                }
            case READ_BY_REGNUM:
                Student student = dao.findByRegNum(request.getNumber());

                if (student != null) {
                    StudentResponse response = new StudentResponse(true, "Etudiant avec le matricule : "+ request.getNumber(), student);
                    return response;
                } else {
                    return new StudentResponse(false, "Aucun étudiant avec ce numéro de matriculation");
                }

            case READ_BY_IDCARD:
                Student stu = dao.findByIdCard(request.get_idCard());
                
                if (stu != null) {
                    StudentResponse response = new StudentResponse(true, "Etudiant avec la CIN : "+ request.get_idCard(), stu);
                    return response;
                } else {
                    return new StudentResponse(false, "Aucun étudiant avec cette carte d'indentité");
                }
                
            case READ_ALL:
                List<Student> studentList = dao.getAllStudents();
                StudentResponse response = new StudentResponse(true, "Liste de tous les étudiants", studentList);
                return response;

            case UPDATE_BY_ID:
                boolean updated = dao.updateStudentById(request.getNumber(), request.getStudent());
                return new StudentResponse(updated, updated ? "✅ Mise à jour de l'étudiant avec l'ID : " + request.getNumber() + "  réussie" : "❌ Mise à jour échouée");

            case UPDATE_BY_REGNUM:
                boolean up = dao.updateStudentByRegNum(request.getNumber(), request.getStudent());
                return new StudentResponse(up, up ? "✅ Mise à jour de l'étudiant avec le matricule : " + request.getNumber() + "  réussie" : "❌ Mise à jour échouée");

            case DELETE_BY_REGNUM:
                boolean deleted = dao.deleteByRegNum(request.getNumber());
                return new StudentResponse(deleted, deleted ? "✅ Étudiant (matricule : "+request.getNumber()+") supprimé" : "❌ Suppression échouée");

            case DELETE_BY_ID:
                boolean deleted_ = dao.deleteById(request.getNumber());
                return new StudentResponse(deleted_, deleted_ ? "✅ Étudiant (identifiant : "+request.getNumber()+") supprimé" : "❌ Suppression échouée");
        
            default:
                return new StudentResponse(false, "❌ Action inconnue");
            }        
    }

}