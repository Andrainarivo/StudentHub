// (Model for JTable)
package mg.eni.studenthub.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel customisé pour afficher une liste d'étudiants dans un JTable.
 * - Chaque colonne correspond à un attribut de Student.
 * - Fournit uniquement l'affichage (pas encore d'édition).
 */
public class StudentTableModel extends AbstractTableModel {

    // Noms des colonnes affichées dans la JTable
    private final String[] columns = {
            "ID", "RegNum", "First Name", "Last Name",
            "Email", "Address", "Level", "ID Card", "Scholarship"
    };

    // Données de la table (liste des étudiants)
    private List<Student> data = new ArrayList<>();

    /**
     * Remplace les étudiants existants par une nouvelle liste.
     * Si null est passé → on réinitialise à une liste vide.
     */
    public void setStudents(List<Student> students) {
        data = (students == null) ? new ArrayList<>() : new ArrayList<>(students);
        fireTableDataChanged(); // Notifie le JTable que les données ont changé
    }

    /**
     * Récupère un étudiant à une ligne donnée (sécurité: retourne null si hors borne).
     */
    public Student getStudentAt(int row) {
        return (row >= 0 && row < data.size()) ? data.get(row) : null;
    }

    // --- Implémentation AbstractTableModel ---
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Student s = data.get(row);
        return switch (col) {
            case 0 -> s.getID();
            case 1 -> s.get_regNum();
            case 2 -> s.get_fname();
            case 3 -> s.get_lname();
            case 4 -> s.get_email();
            case 5 -> s.get_address();
            case 6 -> s.get_level();
            case 7 -> s.get_idCard();
            case 8 -> s.get_scholarship();
            default -> null; // sécurité en cas d'erreur d'index
        };
    }
}
