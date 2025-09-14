package mg.eni.studenthub.view;

import mg.eni.studenthub.model.Student;
import mg.eni.studenthub.model.StudentTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Interface graphique principale pour la gestion des étudiants.
 * Contient :
 *  - un panneau de recherche,
 *  - un formulaire de saisie,
 *  - une table pour lister les étudiants,
 *  - une zone de logs.
 */
public class StudentGUI extends JFrame {
    // Zone de log
    private final JTextArea logArea = new JTextArea(6, 100);

    // Champ de recherche
    private final JTextField searchField = new JTextField(24);

    // Champs du formulaire
    private final JTextField idField = new JTextField(6);
    private final JTextField regNumField = new JTextField(10);
    private final JTextField fnameField = new JTextField(12);
    private final JTextField lnameField = new JTextField(12);
    private final JTextField emailField = new JTextField(18);
    private final JTextField addressField = new JTextField(18);
    private final JTextField levelField = new JTextField(8);
    private final JTextField idCardField = new JTextField(14);
    private final JTextField amountField = new JTextField(10);

    // Table + tri
    private final JTable table;
    private final TableRowSorter<StudentTableModel> sorter;

    // Boutons accessibles au contrôleur
    public final JButton createBtn = new JButton("➕ Add Student");
    public final JButton readByRegBtn = new JButton("🔎 Read by ID / RegNum / IDCard");
    public final JButton readAllBtn = new JButton("📋 Read All");
    public final JButton updateByIdBtn = new JButton("✏️ Update by ID / RegNum");
    public final JButton deleteByIdBtn = new JButton("🗑️ Delete by ID / RegNum");
    public final JButton clearFormBtn = new JButton("🧹 Clear Form");

    public StudentGUI(StudentTableModel model) {
        super("🎓 Student Management");
        this.table = new JTable(model);
        this.sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(8, 8));
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenterSplit(), BorderLayout.CENTER);
        add(buildLogPanel(), BorderLayout.SOUTH);

        installShortcuts();
    }

    // --- Panneau supérieur : recherche + actions rapides ---
    private JComponent buildTopBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        // Zone de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("🔎 Search:"));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
        searchPanel.add(searchField);

        // Bouton Read All
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(readAllBtn);

        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    // --- Centre : formulaire + table ---
    private JComponent buildCenterSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.35);
        split.setLeftComponent(buildFormPanel());
        split.setRightComponent(buildTablePanel());
        return split;
    }

    // --- Formulaire d'édition ---
    private JComponent buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int y = 0;
        addFieldRow(form, c, y++, "ID:", idField);
        addFieldRow(form, c, y++, "Registration Number:", regNumField);
        addFieldRow(form, c, y++, "First Name:", fnameField);
        addFieldRow(form, c, y++, "Last Name:", lnameField);
        addFieldRow(form, c, y++, "Email:", emailField);
        addFieldRow(form, c, y++, "Address:", addressField);
        addFieldRow(form, c, y++, "Level:", levelField);
        addFieldRow(form, c, y++, "ID Card:", idCardField);
        addFieldRow(form, c, y++, "Scholarship Amount:", amountField);

        // Boutons actions
        JPanel btns = new JPanel(new GridLayout(0, 1, 6, 6));
        btns.add(createBtn);
        btns.add(readByRegBtn);
        btns.add(updateByIdBtn);
        btns.add(deleteByIdBtn);
        btns.add(clearFormBtn);

        c.gridx = 0; c.gridy = y; c.gridwidth = 2; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        form.add(btns, c);

        return new JScrollPane(form);
    }

    private void addFieldRow(JPanel panel, GridBagConstraints c, int y, String label, JComponent field) {
        c.gridx = 0; c.gridy = y; c.gridwidth = 1; c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(field, c);
    }

    // --- Table ---
    private JComponent buildTablePanel() {
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(table);
    }

    // --- Zone de log ---
    private JComponent buildLogPanel() {
        logArea.setEditable(false);
        return new JScrollPane(logArea);
    }

    // --- Raccourcis clavier ---
    private void installShortcuts() {
        JRootPane root = getRootPane();
        // Refresh list Ctrl+R
        root.registerKeyboardAction(e -> readAllBtn.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        // New student Ctrl+N
        root.registerKeyboardAction(e -> createBtn.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        // Clear form ESC
        root.registerKeyboardAction(e -> clearFormBtn.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    // --- Filtrage en temps réel ---
    private void filter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    // ===== Méthodes accessibles au contrôleur =====

    /**
     * Construit un objet Student depuis le formulaire.
     * Affiche une erreur si les champs sont invalides.
     */
    public Student buildStudentFromFormOrShowError() {
        try {
            if (isBlank(regNumField) || isBlank(fnameField) || isBlank(lnameField) ||
                isBlank(emailField) || isBlank(addressField) || isBlank(levelField) ||
                isBlank(idCardField) || isBlank(amountField)) {
                JOptionPane.showMessageDialog(this, "⚠️ All fields must be filled.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (!emailField.getText().matches("^.+@.+\\..+$")) {
                JOptionPane.showMessageDialog(this, "✉️ Invalid email format.", "Format Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (!idCardField.getText().matches("\\d{12}")) {
                JOptionPane.showMessageDialog(this, "🆔 ID Card must contain exactly 12 digits.", "Format Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return new Student(
                    parseIntOrThrow(regNumField.getText(), "Registration Number must be an integer."),
                    fnameField.getText(),
                    lnameField.getText(),
                    emailField.getText(),
                    addressField.getText(),
                    levelField.getText(),
                    idCardField.getText(),
                    amountField.getText()
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "🔢 " + ex.getMessage(), "Format Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public Integer getIdField() {
        String t = idField.getText().trim();
        if (t.isEmpty()) return null;
        try { return Integer.parseInt(t); }
        catch (NumberFormatException ex) { return null; }
    }

    public Integer getRegNumField() {
        String t = regNumField.getText().trim();
        if (t.isEmpty()) return null;
        try { return Integer.parseInt(t); }
        catch (NumberFormatException ex) { return null; }
    }

    public String getIdCardField() {
        String t = idCardField.getText().trim();
        return t.isEmpty() ? null : t;
    }

    private boolean isBlank(JTextField f) {
        return f.getText() == null || f.getText().isBlank();
    }

    private int parseIntOrThrow(String s, String message) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new NumberFormatException(message); }
    }

    public void clearForm() {
        idField.setText("");
        regNumField.setText("");
        fnameField.setText("");
        lnameField.setText("");
        emailField.setText("");
        addressField.setText("");
        levelField.setText("");
        idCardField.setText("");
        amountField.setText("");
    }

    /**
     * Ajoute un message dans la zone de log.
     */
    public void log(String message) {
        if (message == null || message.isBlank()) return;
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
