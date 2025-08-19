package mg.eni.studenthub.client;

import javax.swing.*;
//import java.util.logging.Logger;
import mg.eni.studenthub.utils.DB_Connection;

public class App {
    //private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        // (Optional) Modern Look & Feel
        UIUtils.installFlatLaf();
        //UIUtils.installSystemLookAndFeel();

        if (!StudentClient.isServerAvailable() || !DB_Connection.isDbAvailable()) {
            System.out.println("⛔ Le serveur TLS ou la base de données n'est pas disponible. Les requêtes seront sauvegardées localement.");
        }

        RetryThread retryThread = new RetryThread();
        retryThread.setDaemon(true);
        retryThread.start();

        SwingUtilities.invokeLater(() -> {
            StudentTableModel tableModel = new StudentTableModel();
            StudentGUI view = new StudentGUI(tableModel);
            new StudentController(view, tableModel); // Controller wires listeners
            view.setVisible(true);
        });
    }
}



