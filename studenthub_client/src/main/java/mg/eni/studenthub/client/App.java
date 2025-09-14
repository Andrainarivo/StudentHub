package mg.eni.studenthub.client;

import javax.swing.*;
import mg.eni.studenthub.utils.DB_Connection;
import mg.eni.studenthub.utils.UIUtils;
import mg.eni.studenthub.view.LoginFrame;
import mg.eni.studenthub.view.StudentGUI;
import mg.eni.studenthub.controller.StudentController;
import mg.eni.studenthub.model.StudentTableModel;

public class App {

    public static void main(String[] args) {
        // Modern Look & Feel
        UIUtils.installFlatLaf();

        if (!StudentClient.isServerAvailable() || !DB_Connection.isDbAvailable()) {
            System.out.println("⛔ Le serveur TLS ou la base de données n'est pas disponible. Les requêtes seront sauvegardées localement.");
        }

        // Thread de réssai (pour envoi différé quand le serveur redevient dispo)
        RetryThread retryThread = new RetryThread();
        retryThread.setDaemon(true);
        retryThread.start();

        // login
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(() -> {
                // Code exécuté après authentification réussie
                StudentTableModel tableModel = new StudentTableModel();
                StudentGUI view = new StudentGUI(tableModel);
                new StudentController(view, tableModel); // Controller wires listeners
                view.setVisible(true);
            });
            loginFrame.setVisible(true);
        });
    }
}



