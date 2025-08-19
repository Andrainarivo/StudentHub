// (Controller) - wires user actions to services, uses SwingWorker
// ---------------------------------------------
package mg.eni.studenthub.client;

import mg.eni.studenthub.model.Student;
import mg.eni.studenthub.shared.StudentResponse;

import javax.swing.*;

public class StudentController {
    private final StudentGUI view;
    private final StudentTableModel tableModel;

    public StudentController(StudentGUI view, StudentTableModel model) {
        this.view = view;
        this.tableModel = model;
        wireActions();
    }

    private void wireActions() {
        view.readAllBtn.addActionListener(e -> loadAllStudents());
        view.createBtn.addActionListener(e -> createStudent());
        view.readByRegBtn.addActionListener(e -> readByRegNum());
        view.updateByIdBtn.addActionListener(e -> updateById());
        view.deleteByIdBtn.addActionListener(e -> deleteById());
        view.clearFormBtn.addActionListener(e -> view.clearForm());
    }

    private void loadAllStudents() {
        new SwingWorker<StudentResponse, Void>() {
            @Override protected StudentResponse doInBackground() { return StudentService.readAllStudents(); }
            @Override protected void done() {
                try {
                    StudentResponse res = get();
                    if (res != null) {
                        tableModel.setStudents(res.getStudentList());
                        view.log("📥 " + res.getMessage());
                    } else view.log("❌ Aucun retour du serveur.");
                } catch (Exception ex) {
                    view.log("❌ Erreur chargement: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void createStudent() {
        Student s = view.buildStudentFromFormOrShowError();
        if (s == null) return;
        new SwingWorker<StudentResponse, Void>() {
            @Override protected StudentResponse doInBackground() { return StudentService.createStudent(s); }
            @Override protected void done() {
                try {
                    StudentResponse res = get();
                    view.log("➕ " + (res != null ? res.getMessage() : "Aucun retour"));
                    loadAllStudents();
                } catch (Exception ex) {
                    view.log("❌ Erreur création: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void readByRegNum() {
        Integer regNum = view.getRegNumFieldOrWarn();
        if (regNum == null) return;
        new SwingWorker<StudentResponse, Void>() {
            @Override protected StudentResponse doInBackground() { return StudentService.readStudentByRegNum(regNum); }
            @Override protected void done() {
                try {
                    StudentResponse res = get();
                    if (res != null && res.getStudent() != null) {
                        tableModel.setStudents(java.util.List.of(res.getStudent()));
                        view.log("🔎 " + res.getMessage());
                    } else {
                        view.log("ℹ️ Aucun étudiant trouvé pour RegNum=" + regNum);
                    }
                } catch (Exception ex) {
                    view.log("❌ Erreur lecture: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void updateById() {
        Integer id = view.getIdFieldOrWarn();
        if (id == null) return;
        Student s = view.buildStudentFromFormOrShowError();
        if (s == null) return;
        new SwingWorker<StudentResponse, Void>() {
            @Override protected StudentResponse doInBackground() { return StudentService.updateStudent(id, s); }
            @Override protected void done() {
                try {
                    StudentResponse res = get();
                    view.log("✏️ " + (res != null ? res.getMessage() : "Aucun retour"));
                    loadAllStudents();
                } catch (Exception ex) {
                    view.log("❌ Erreur mise à jour: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void deleteById() {
        Integer id = view.getIdFieldOrWarn();
        if (id == null) return;
        int confirm = JOptionPane.showConfirmDialog(null, "Supprimer l'enregistrement ID=" + id + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        new SwingWorker<StudentResponse, Void>() {
            @Override protected StudentResponse doInBackground() { return StudentService.deleteStudent(id); }
            @Override protected void done() {
                try {
                    StudentResponse res = get();
                    view.log("🗑️ " + (res != null ? res.getMessage() : "Aucun retour"));
                    loadAllStudents();
                } catch (Exception ex) {
                    view.log("❌ Erreur suppression: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
