package mg.eni.studenthub.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
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
import mg.eni.studenthub.shared.StudentRequest;
import mg.eni.studenthub.shared.StudentResponse;
import mg.eni.studenthub.utils.DB_Connection;
import mg.eni.studenthub.utils.XmlUtil;
import mg.eni.studenthub.utils.TLSUtil;

/**
 * Client pour communiquer avec le serveur StudentHub en TLS mutuel (mTLS).
 * - Sauvegarde les requêtes localement si serveur/DB indisponibles
 * - Utilise TLS avec authentification client/serveur (mTLS)
 */
public class StudentClient {
    private static final Logger LOGGER = Logger.getLogger(StudentClient.class.getName());

    private static final String SERVER_ADDRESS = ConfigLoader.get("server.host");
    private static final int SERVER_PORT = ConfigLoader.getInt("server.port", 0);
    private static final String REQ_FOLDER = ConfigLoader.get("request.folder");

    /**
     * Envoie une requête StudentRequest au serveur, ou la sauvegarde localement si offline.
     *
     * @param request la requête étudiant à envoyer
     * @return StudentResponse reçue du serveur ou null si échec
     */
    public static StudentResponse sendRequest(StudentRequest request) {
        // Si DB ou serveur indisponible, on passe en mode offline
        if (!DB_Connection.isDbAvailable() || !isServerAvailable()) {
            try {
                Files.createDirectories(Paths.get(REQ_FOLDER));
                String fileName = REQ_FOLDER + "/request-" + request.getAction() + "-" + System.currentTimeMillis() + ".xml";
                XmlUtil.saveRequestToXml(request, fileName);
                LOGGER.info("Request saved locally: " + fileName);
                return new StudentResponse(false, "Request saved locally: " + fileName);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to save request locally", ex);
            }
            return null;
        }

        // Sinon, tentative de connexion mTLS
        try {
            SSLContext sslContext = TLSUtil.createSSLContext();
            SSLSocketFactory factory = sslContext.getSocketFactory();

            try (SSLSocket sslSocket = (SSLSocket) factory.createSocket(SERVER_ADDRESS, SERVER_PORT);
                 ObjectOutputStream out = new ObjectOutputStream(sslSocket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(sslSocket.getInputStream())) {

                LOGGER.info("mTLS connection established with server.");
                out.writeObject(request);
                out.flush();

                Object response = in.readObject();
                if (response instanceof StudentResponse studentResponse) {
                    LOGGER.info("Response received from server.");
                    return studentResponse;
                } else {
                    LOGGER.warning("Unexpected response type from server.");
                }
            }
        } catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "TLS connection failed", e);
        } catch (Exception e) {
            
        }
        return null;
    }

    /**
     * Vérifie si le serveur TLS est disponible en tentant un handshake.
     *
     * @return true si le serveur est accessible en TLS, sinon false
     */
    public static boolean isServerAvailable() {
        try {
            SSLContext sslContext = TLSUtil.createSSLContext();
            SSLSocketFactory factory = sslContext.getSocketFactory();

            try (SSLSocket sslSocket = (SSLSocket) factory.createSocket(SERVER_ADDRESS, SERVER_PORT)) {
                sslSocket.startHandshake();
                return true;
            }
        } catch (Exception e) {
            LOGGER.warning("TLS server unavailable: " + e.getMessage());
            return false;
        }
    }

}
