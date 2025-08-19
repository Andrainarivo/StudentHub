// (Model for JTable)
// ---------------------------------------------
package mg.eni.studenthub.client;

import mg.eni.studenthub.model.Student;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class StudentTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "RegNum", "First Name", "Last Name", "Email", "Address", "Level", "ID Card", "Amount"};
    private List<Student> data = new ArrayList<>();

    public void setStudents(List<Student> students) {
        data = (students == null) ? new ArrayList<>() : new ArrayList<>(students);
        fireTableDataChanged();
    }

    public Student getStudentAt(int row) { return (row >= 0 && row < data.size()) ? data.get(row) : null; }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int col) { return columns[col]; }

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
            default -> null;
        };
    }
}