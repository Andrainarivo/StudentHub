package mg.eni.studenthub.view;

import mg.eni.studenthub.auth.UserAuthService;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JLabel statusLabel;

    // Callback appelé après succès de connexion
    private final Runnable onLoginSuccess;

    private static final Logger LOGGER = Logger.getLogger(LoginFrame.class.getName());

    public LoginFrame(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;

        setTitle("StudentHub - Login");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Formulaire ---
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        // --- Bouton + statut ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        loginButton = new JButton("Login");
        bottomPanel.add(loginButton, BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Action bouton
        loginButton.addActionListener(e -> attemptLogin());
    }

    /**
     * Tentative de connexion : vérifie les champs, appelle UserAuthService,
     * puis redirige vers l’application principale en cas de succès.
     */
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validation simple des champs
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠️ Please fill in all fields.");
            LOGGER.warning("⚠️ Login attempt with empty fields.");
            return;
        }

        // Authentification
        boolean success = UserAuthService.authenticate(username, password);

        if (success) {
            statusLabel.setText("✅ Login successful!");
            LOGGER.info("✅ User '" + username + "' authenticated successfully.");

            SwingUtilities.invokeLater(() -> {
                dispose(); // fermer la fenêtre de login
                onLoginSuccess.run(); // lancer l’app principale
            });
        } else {
            statusLabel.setText("⛔ Invalid credentials.");
            LOGGER.warning("⛔ Failed login attempt for username: " + username);
        }
    }
}
