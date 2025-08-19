package mg.eni.studenthub.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import mg.eni.studenthub.config.ConfigLoader;
import mg.eni.studenthub.config.LoggerConfig;
import mg.eni.studenthub.shared.StudentRequest;
import mg.eni.studenthub.shared.StudentResponse;
import mg.eni.studenthub.shared.StudentRequest.Action;
import mg.eni.studenthub.utils.DB_Connection;
import mg.eni.studenthub.utils.XmlUtil;

public class RetryThread extends Thread {
    
    private static final Logger LOGGER = Logger.getLogger(RetryThread.class.getName());
    private static final String SERVER_HOST = ConfigLoader.get("server.host");
    private static final int SERVER_PORT = ConfigLoader.getInt("server.port", 0);
    private static final int CHECK_INTERVAL_MS = 5000;
    private static final String REQ_FOLDER = ConfigLoader.get("request.folder");
    private static final String RES_FOLDER = ConfigLoader.get("response.folder");

    @Override
    public void run() {
        LoggerConfig.setup();

        while (true) {
            try {
                File[] files = new File(REQ_FOLDER).listFiles((dir, name) -> name.endsWith(".xml"));

                // 🔍 1. S'il n'y a aucun fichier en attente, inutile de contacter le serveur
                if (files == null || files.length == 0) {
                    LOGGER.fine("😴 Aucun fichier XML en attente.");
                    Thread.sleep(CHECK_INTERVAL_MS);
                    continue;
                }

                // 🔐 2. S'il y a des fichiers, on vérifie la disponibilité du serveur
                if (files.length > 0 && StudentClient.isServerAvailable() && DB_Connection.isDbAvailable()) {
                    LOGGER.info("🔄 Connexion TLS détectée. Traitement des fichiers XML en attente...");

                    for (File file : files) {
                        StudentRequest request = XmlUtil.loadRequestFromXml(file.getAbsolutePath());
                        Action action = request.getAction();

                        StudentResponse response = sendRequestToServer(request);

                        if (response.isSuccess()) {
                            LOGGER.info("✅ Requête " + file.getName() + " traitée avec succès.");
                            file.delete();
                        } else {
                            LOGGER.warning("❌ Échec d’envoi pour : " + file.getName());
                        }

                        // sauvegarder la réponse en XML si c'est un READ ou READ_ALL
                        switch (action) {
                            case READ_ALL:
                                String filename = RES_FOLDER + "/response_READ_ALL_" + System.currentTimeMillis() + ".xml";

                                try {
                                    Files.createDirectories(Paths.get(RES_FOLDER));
                                    XmlUtil.saveStudentResponseToXml(response, filename);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                                
                            case READ_BY_IDCARD:
                                String filename2 = RES_FOLDER + "/response_IDCard_" + request.get_idCard() + ".xml";

                                try {
                                    Files.createDirectories(Paths.get(RES_FOLDER));
                                    XmlUtil.saveStudentResponseToXml(response, filename2);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case READ_BY_REGNUM:
                                String filename3 = RES_FOLDER + "/response_RegNum_" + request.getNumber() + ".xml";

                                try {
                                    Files.createDirectories(Paths.get(RES_FOLDER));
                                    XmlUtil.saveStudentResponseToXml(response, filename3);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case READ_BY_ID:
                                String filename4 = RES_FOLDER + "/response_ID_" + request.getNumber() + ".xml";

                                try {
                                    Files.createDirectories(Paths.get(RES_FOLDER));
                                    XmlUtil.saveStudentResponseToXml(response, filename4);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        
                            default:
                                break;
                        }
                    }

                } else {
                    LOGGER.warning("🚫 Serveur TLS indisponible. Nouvelle tentative dans 5 secondes...");
                }

                Thread.sleep(CHECK_INTERVAL_MS);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "⛔ Erreur dans RetryThread : " + e.getMessage(), e);
            }
        }
    }


    private StudentResponse sendRequestToServer(StudentRequest request) {
    try {
        // 🔐 Lecture des paramètres TLS mTLS
        String keystorePath = ConfigLoader.get("client_keystore.path");
        String keystorePassword = ConfigLoader.get("client_keystore.password");
        String truststorePath = ConfigLoader.get("client_truststore.path");
        String truststorePassword = ConfigLoader.get("client_truststore.password");

        // 1. Charger le truststore (confiance en le serveur)
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream trustFis = new FileInputStream(truststorePath)) {
            trustStore.load(trustFis, truststorePassword.toCharArray());
        }

        // 2. Charger le keystore (certificat du client)
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream keyFis = new FileInputStream(keystorePath)) {
            keyStore.load(keyFis, keystorePassword.toCharArray());
        }

        // 3. Init du TrustManager
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // 4. Init du KeyManager
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keystorePassword.toCharArray());

        // 5. Création du contexte SSL mutualisé
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        SSLSocketFactory factory = sslContext.getSocketFactory();

        // 6. Communication TLS
        try (SSLSocket sslSocket = (SSLSocket) factory.createSocket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(sslSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(sslSocket.getInputStream())) {

            out.writeObject(request);
            out.flush();

            Object response = in.readObject();
            if (response instanceof StudentResponse studentResponse) {
                studentResponse.setSuccess(true);
                return studentResponse;
            } else {
                LOGGER.warning("⚠️ Réponse inattendue (type inconnu) lors du retry.");
            }
        }

    } catch (Exception e) {
        LOGGER.warning("❌ Envoi échoué (RetryThread) : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        //return false;
    }
    return null;
}

}
