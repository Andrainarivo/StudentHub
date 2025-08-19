package mg.eni.studenthub.client;

import mg.eni.studenthub.model.Student;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyEvent;

public class StudentGUI extends JFrame {
    // Top-level components
    private final JTextArea logArea = new JTextArea(6, 100);
    private final JTextField searchField = new JTextField(24);

    // Form fields
    private final JTextField idField = new JTextField(6);
    private final JTextField regNumField = new JTextField(10);
    private final JTextField fnameField = new JTextField(12);
    private final JTextField lnameField = new JTextField(12);
    private final JTextField emailField = new JTextField(18);
    private final JTextField addressField = new JTextField(18);
    private final JTextField levelField = new JTextField(8);
    private final JTextField idCardField = new JTextField(14);
    private final JTextField amountField = new JTextField(10);

    // Table
    private final JTable table;
    //private final StudentTableModel tableModel;
    private final TableRowSorter<StudentTableModel> sorter;

    // Buttons (exposed for controller wiring)
    public final JButton createBtn = new JButton("Créer");
    public final JButton readByRegBtn = new JButton("Lire par RegNum");
    public final JButton readAllBtn = new JButton("Tout lire");
    public final JButton updateByIdBtn = new JButton("Mettre à jour (ID)");
    public final JButton deleteByIdBtn = new JButton("Supprimer (ID)");
    public final JButton clearFormBtn = new JButton("Vider formulaire");

    public StudentGUI(StudentTableModel model) {
        super("🎓 Student Management");
        //this.tableModel = model;
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

    private JComponent buildTopBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("🔎 Recherche:"));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
        searchPanel.add(searchField);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(readAllBtn);

        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JComponent buildCenterSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.35); // left panel size
        split.setLeftComponent(buildFormPanel());
        split.setRightComponent(buildTablePanel());
        return split;
    }

    private JComponent buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int y = 0;
        addFieldRow(form, c, y++, "ID:", idField);
        addFieldRow(form, c, y++, "RegNum:", regNumField);
        addFieldRow(form, c, y++, "First Name:", fnameField);
        addFieldRow(form, c, y++, "Last Name:", lnameField);
        addFieldRow(form, c, y++, "Email:", emailField);
        addFieldRow(form, c, y++, "Address:", addressField);
        addFieldRow(form, c, y++, "Level:", levelField);
        addFieldRow(form, c, y++, "ID Card:", idCardField);
        addFieldRow(form, c, y++, "Scholarship Amount:", amountField);

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

    private JComponent buildTablePanel() {
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(table);
    }

    private JComponent buildLogPanel() {
        logArea.setEditable(false);
        return new JScrollPane(logArea);
    }

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

    private void filter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    // ===== Helpers visible par le contrôleur =====
    public Student buildStudentFromFormOrShowError() {
        try {
            if (isBlank(regNumField) || isBlank(fnameField) || isBlank(lnameField) ||
                isBlank(emailField) || isBlank(addressField) || isBlank(levelField) ||
                isBlank(idCardField) || isBlank(amountField)) {
                JOptionPane.showMessageDialog(this, "❗ Tous les champs doivent être remplis.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (!emailField.getText().matches("^.+@.+\\..+$")) {
                JOptionPane.showMessageDialog(this, "✉️ Email invalide.", "Erreur de format", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (!idCardField.getText().matches("\\d{12}")) {
                JOptionPane.showMessageDialog(this, "🆔 ID Card doit contenir exactement 12 chiffres.", "Erreur de format", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return new Student(
                    parseIntOrThrow(regNumField.getText(), "Le numéro d'enregistrement doit être un entier."),
                    fnameField.getText(),
                    lnameField.getText(),
                    emailField.getText(),
                    addressField.getText(),
                    levelField.getText(),
                    idCardField.getText(),
                    amountField.getText()
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "🔢 " + ex.getMessage(), "Erreur de format", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public Integer getIdFieldOrWarn() {
        String t = idField.getText().trim();
        if (t.isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'ID est requis pour cette action.", "Champ requis", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try { return Integer.parseInt(t); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "L'ID doit être un entier.", "Erreur de format", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public Integer getRegNumFieldOrWarn() {
        String t = regNumField.getText().trim();
        if (t.isEmpty()) {
            JOptionPane.showMessageDialog(this, "RegNum est requis.", "Champ requis", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try { return Integer.parseInt(t); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "RegNum doit être un entier.", "Erreur de format", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private boolean isBlank(JTextField f) { return f.getText() == null || f.getText().isBlank(); }
    private int parseIntOrThrow(String s, String message) { try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { throw new NumberFormatException(message); } }

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

    public void log(String message) {
        if (message == null || message.isBlank()) return;
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}