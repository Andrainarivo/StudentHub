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

public class StudentClient {
    private static final Logger LOGGER = Logger.getLogger(StudentClient.class.getName());
    private static final String SERVER_ADDRESS = ConfigLoader.get("server.host");
    private static final int SERVER_PORT = ConfigLoader.getInt("server.port", 0);
    private static final String REQ_FOLDER = ConfigLoader.get("request.folder");

    public static StudentResponse sendRequest(StudentRequest request) {

    if (!DB_Connection.isDbAvailable() || !isServerAvailable()) {
        try {
            Files.createDirectories(Paths.get(REQ_FOLDER));
            String fileName = REQ_FOLDER + "/request-"+request.getAction()+ System.currentTimeMillis() + ".xml";
            XmlUtil.saveRequestToXml(request, fileName);
            LOGGER.info("💾 Requête " + fileName + " sauvegardée localement");
            return new StudentResponse(false, "💾 Requête " + fileName + " sauvegardée localement");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "❌ Erreur de sauvegarde XML", ex.getMessage());
        }
        return null;
    } else {
        try {
            // Lecture des paramètres de configuration du truststore
            String truststorePath = ConfigLoader.get("client_truststore.path");
            String truststorePassword = ConfigLoader.get("client_truststore.password");

            // Lecture des paramètres de configuration du keystore (authentification client)
            String keystorePath = ConfigLoader.get("client_keystore.path");
            String keystorePassword = ConfigLoader.get("client_keystore.password");

            // Chargement du truststore (pour vérifier le certificat serveur)
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream trustFis = new FileInputStream(truststorePath)) {
                trustStore.load(trustFis, truststorePassword.toCharArray());
            }

            // Chargement du keystore (pour fournir le certificat client)
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream keyFis = new FileInputStream(keystorePath)) {
                keyStore.load(keyFis, keystorePassword.toCharArray());
            }

            // Init du TrustManager (vérifie le certificat du serveur)
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Init du KeyManager (fournit le certificat client)
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keystorePassword.toCharArray());

            // Init du contexte SSL avec les deux managers
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            SSLSocketFactory factory = sslContext.getSocketFactory();

            try (SSLSocket sslSocket = (SSLSocket) factory.createSocket(SERVER_ADDRESS, SERVER_PORT);
                ObjectOutputStream out = new ObjectOutputStream(sslSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(sslSocket.getInputStream())) {

                LOGGER.info("🔐 Connexion mTLS établie avec le serveur.");
                out.writeObject(request);
                out.flush();
                Object response = in.readObject();
                if (response instanceof StudentResponse) {
                    LOGGER.info("✅ Réponse reçue du serveur.");
                    return (StudentResponse) response;
                } else {
                    LOGGER.warning("⚠️ Réponse inattendue du serveur.");
                }
            }
        } catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "📴 Connexion TLS échouée : " + e.getMessage());
        }

    }
    return null;
    }

    public static boolean isServerAvailable() {
        try {
            String truststorePath = ConfigLoader.get("client_truststore.path");
            String truststorePassword = ConfigLoader.get("client_truststore.password");
            String keystorePath = ConfigLoader.get("client_keystore.path");
            String keystorePassword = ConfigLoader.get("client_keystore.password");
    
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream trustFis = new FileInputStream(truststorePath)) {
                trustStore.load(trustFis, truststorePassword.toCharArray());
            }
    
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream keyFis = new FileInputStream(keystorePath)) {
                keyStore.load(keyFis, keystorePassword.toCharArray());
            }
    
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
    
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keystorePassword.toCharArray());
    
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
    
            SSLSocketFactory factory = sslContext.getSocketFactory();
    
            try (SSLSocket sslSocket = (SSLSocket) factory.createSocket(SERVER_ADDRESS, SERVER_PORT)) {
                sslSocket.startHandshake();
                return true;
            }
    
        } catch (Exception e) {
            LOGGER.warning("❌ TLS Server indisponible : " + e.getMessage());
            return false;
        }
    }
}