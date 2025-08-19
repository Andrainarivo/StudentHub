package mg.eni.studenthub.server;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import mg.eni.studenthub.config.ConfigLoader;

public class StudentServer {
    private static final Logger LOGGER = Logger.getLogger(StudentServer.class.getName());

    public static void main(String[] args) {
        try {
            // Chargement de la configuration depuis config.properties
            String keystorePath = ConfigLoader.get("server_keystore.path");
            String keystorePassword = ConfigLoader.get("server_keystore.password");

            String truststorePath = ConfigLoader.get("server_truststore.path");
            String truststorePassword = ConfigLoader.get("server_truststore.password");

            int port = ConfigLoader.getInt("server.port", 0);

            // === Chargement du keystore (identité du serveur)
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(keystorePath)) {
                keyStore.load(fis, keystorePassword.toCharArray());
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, keystorePassword.toCharArray());

            // === Chargement du truststore (certificats des clients à approuver)
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(truststorePath)) {
                trustStore.load(fis, truststorePassword.toCharArray());
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(trustStore);

            // === Initialisation du contexte SSL avec clé serveur et certificat client à vérifier
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // === Création d'un socket SSL sécurisé
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

            // On force l'authentification du certificat client
            serverSocket.setNeedClientAuth(true);  // <== Mutual TLS

            LOGGER.info("🟢 Serveur mTLS démarré sur le port " + port);

            while (true) {
                SSLSocket sslSocket = (SSLSocket) serverSocket.accept();
                LOGGER.info("🔐 Connexion mTLS acceptée depuis : " + sslSocket.getInetAddress());
                new Thread(new StudentHandler(sslSocket)).start();
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur mTLS côté serveur : " + e.getMessage());
        }
    }
}

