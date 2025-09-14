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
import mg.eni.studenthub.utils.TLSUtil;

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

                // 1. Aucun fichier en attente
                if (files == null || files.length == 0) {
                    //LOGGER.fine("No pending XML request files.");
                    Thread.sleep(CHECK_INTERVAL_MS);
                    continue;
                }

                // 2. Vérif serveur & DB
                if (StudentClient.isServerAvailable() && DB_Connection.isDbAvailable()) {
                    //LOGGER.info("TLS connection available. Processing pending XML requests...");

                    for (File file : files) {
                        StudentRequest request = XmlUtil.loadRequestFromXml(file.getAbsolutePath());
                        if (request == null) {
                            LOGGER.warning("Invalid XML request file: " + file.getName());
                            continue;
                        }

                        StudentResponse response = sendRequestToServer(request);

                        if (response != null && response.isSuccess()) {
                            LOGGER.info("Request " + file.getName() + " processed successfully.");
                            if (!file.delete()) {
                                LOGGER.warning("Failed to delete processed request file: " + file.getName());
                            }
                        } else {
                            LOGGER.warning("Failed to process request: " + file.getName());
                        }

                        // Save response if it's a READ action
                        switch (request.getAction()) {
                            case READ_ALL -> saveResponse(response, RES_FOLDER + "/response_READ_ALL_" + System.currentTimeMillis() + ".xml");
                            case READ_BY_IDCARD -> saveResponse(response, RES_FOLDER + "/response_IDCard_" + request.get_idCard() + ".xml");
                            case READ_BY_REGNUM -> saveResponse(response, RES_FOLDER + "/response_RegNum_" + request.getNumber() + ".xml");
                            case READ_BY_ID -> saveResponse(response, RES_FOLDER + "/response_ID_" + request.getNumber() + ".xml");
                            default -> { /* no XML response to save */ }
                        }
                    }

                } else {
                    LOGGER.warning("TLS server unavailable. Retrying in 5 seconds...");
                }

                Thread.sleep(CHECK_INTERVAL_MS);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error in RetryThread: " + e.getMessage(), e);
            }
        }
    }

    // Factorisation de la sauvegarde réponse
    private void saveResponse(StudentResponse response, String filePath) {
        try {
            Files.createDirectories(Paths.get(RES_FOLDER));
            XmlUtil.saveStudentResponseToXml(response, filePath);
            LOGGER.info("Response saved to: " + filePath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save response to XML: " + filePath, e);
        }
    }

    // Sécurisation du send
    private StudentResponse sendRequestToServer(StudentRequest request) {
        try {
            // TLS handshake setup (keystore/truststore) ...
            SSLContext sslContext = TLSUtil.createSSLContext();
            SSLSocketFactory factory = sslContext.getSocketFactory();

            try (SSLSocket sslSocket = (SSLSocket) factory.createSocket(SERVER_HOST, SERVER_PORT);
                ObjectOutputStream out = new ObjectOutputStream(sslSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(sslSocket.getInputStream())) {

                out.writeObject(request);
                out.flush();

                Object response = in.readObject();
                if (response instanceof StudentResponse studentResponse) {
                    return studentResponse;
                } else {
                    LOGGER.warning("Unexpected response type received during retry.");
                    return new StudentResponse(false, "Unexpected response type");
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "RetryThread send failed: " + e.getMessage(), e);
            return new StudentResponse(false, "Retry failed: " + e.getMessage());
        }
    }

}