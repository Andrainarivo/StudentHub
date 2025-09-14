package mg.eni.studenthub.controller;

import mg.eni.studenthub.model.Student;
import mg.eni.studenthub.model.StudentTableModel;
import mg.eni.studenthub.shared.StudentResponse;
import mg.eni.studenthub.view.StudentGUI;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class StudentController {
    private final StudentGUI view;
    private final StudentTableModel tableModel;
    private static final Logger LOGGER = Logger.getLogger(StudentController.class.getName());

    public StudentController(StudentGUI view, StudentTableModel model) {
        this.view = view;
        this.tableModel = model;
        wireActions();
    }

    /**
     * Association des actions de la vue aux méthodes du contrôleur.
     * Chaque bouton déclenche une opération CRUD via StudentService.
     */
    private void wireActions() {
        view.readAllBtn.addActionListener(e -> loadAllStudents());
        view.createBtn.addActionListener(e -> createStudent());
        view.readByRegBtn.addActionListener(e -> readByRegNum_or_IdCard_or_Id());
        view.updateByIdBtn.addActionListener(e -> updateById_or_RegNum());
        view.deleteByIdBtn.addActionListener(e -> deleteById_or_RegNum());
        view.clearFormBtn.addActionListener(e -> view.clearForm());
    }

    /**
     * Utilitaire : exécuter une tâche asynchrone via SwingWorker
     * @param task : opération (appel StudentService)
     * @param onSuccess : callback en cas de succès
     * @param errorMessage : message affiché/loggué en cas d'erreur
     */
    private void runAsync(Callable<StudentResponse> task, java.util.function.Consumer<StudentResponse> onSuccess, String errorMessage) {
        new SwingWorker<StudentResponse, Void>() {
            @Override protected StudentResponse doInBackground() throws Exception {
                return task.call();
            }
            @Override protected void done() {
                try {
                    StudentResponse res = get();
                    if (res != null) {
                        onSuccess.accept(res);
                        LOGGER.info("✅ Operation successful: " + res.getMessage());
                        view.log("✅ " + res.getMessage());
                    } else {
                        LOGGER.warning("⚠️ No response received from server.");
                        view.log("⚠️ No response received from server.");
                    }
                } catch (Exception ex) {
                    LOGGER.severe("❌ " + errorMessage + " - " + ex.getMessage());
                    view.log("❌ " + errorMessage + ": " + ex.getMessage());
                }
            }
        }.execute();
    }

    /**
     * Lecture de tous les étudiants (READ_ALL)
     */
    private void loadAllStudents() {
        runAsync(
            StudentService::readAllStudents,
            res -> tableModel.setStudents(res.getStudentList()),
            "Error while loading students"
        );
    }

    /**
     * Création d’un nouvel étudiant (CREATE)
     */
    private void createStudent() {
        Student s = view.buildStudentFromFormOrShowError();
        if (s == null) return;

        runAsync(
            () -> StudentService.createStudent(s),
            res -> loadAllStudents(),
            "Error while creating student"
        );
    }

    /**
     * Lecture d’un étudiant selon l’un des identifiants possibles :
     * - ID Card
     * - ID numérique
     * - Numéro de registre
     */
    private void readByRegNum_or_IdCard_or_Id() {
        Integer regNum = view.getRegNumField();
        Integer id = view.getIdField();
        String idCard = view.getIdCardField();

        if (idCard != null) {
            runAsync(
                () -> StudentService.readStudentByIdCard(idCard),
                this::setSingleStudentInTable,
                "Error while reading by ID Card"
            );
        } else if (id != null) {
            runAsync(
                () -> StudentService.readStudentById(id),
                this::setSingleStudentInTable,
                "Error while reading by ID"
            );
        } else if (regNum != null) {
            runAsync(
                () -> StudentService.readStudentByRegNum(regNum),
                this::setSingleStudentInTable,
                "Error while reading by RegNum"
            );
        }
    }

    /**
     * Mise à jour d’un étudiant, en fonction :
     * - de l’ID numérique
     * - ou du RegNum
     */
    private void updateById_or_RegNum() {
        Integer id = view.getIdField();
        Integer regNum = view.getRegNumField();
        Student s = view.buildStudentFromFormOrShowError();
        if (s == null) return;

        if (regNum != null) {
            runAsync(
                () -> StudentService.updateStudentByRegNum(regNum, s),
                res -> loadAllStudents(),
                "Error while updating by RegNum"
            );
        } else if (id != null) {
            runAsync(
                () -> StudentService.updateStudentById(id, s),
                res -> loadAllStudents(),
                "Error while updating by ID"
            );
        }
    }

    /**
     * Suppression d’un étudiant en fonction :
     * - de l’ID numérique
     * - ou du RegNum
     */
    private void deleteById_or_RegNum() {
        Integer id = view.getIdField();
        Integer regNum = view.getRegNumField();

        if (regNum != null) {
            int confirm = JOptionPane.showConfirmDialog(null,
                "Delete record with REGNUM=" + regNum + " ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            runAsync(
                () -> StudentService.deleteStudentByRegNum(regNum),
                res -> loadAllStudents(),
                "Error while deleting by RegNum"
            );
        } else if (id != null) {
            int confirm = JOptionPane.showConfirmDialog(null,
                "Delete record with ID=" + id + " ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            runAsync(
                () -> StudentService.deleteStudentById(id),
                res -> loadAllStudents(),
                "Error while deleting by ID"
            );
        }
    }

    /**
     * Utilitaire : afficher un seul étudiant dans le tableau
     */
    private void setSingleStudentInTable(StudentResponse res) {
        if (res != null && res.getStudent() != null) {
            tableModel.setStudents(List.of(res.getStudent()));
        } else {
            LOGGER.info("ℹ️ No student found for given criteria.");
            view.log("ℹ️ No student found for given criteria.");
        }
    }
}
