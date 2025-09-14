package mg.eni.studenthub.utils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import mg.eni.studenthub.config.ConfigLoader;

public class TLSUtil {

    private TLSUtil() {
        // Prevent instantiation
    }

    public static SSLContext createSSLContext() throws Exception {
        // Load keystore & truststore paths/passwords from config
        String keystorePath = ConfigLoader.get("client_keystore.path");
        String keystorePassword = ConfigLoader.get("client_keystore.password");
        String truststorePath = ConfigLoader.get("client_truststore.path");
        String truststorePassword = ConfigLoader.get("client_truststore.password");

        // Load truststore (server certificate)
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream trustFis = new FileInputStream(truststorePath)) {
            trustStore.load(trustFis, truststorePassword.toCharArray());
        }

        // Load keystore (client certificate)
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream keyFis = new FileInputStream(keystorePath)) {
            keyStore.load(keyFis, keystorePassword.toCharArray());
        }

        // Init TrustManager
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Init KeyManager
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keystorePassword.toCharArray());

        // Create SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return sslContext;
    }
}
