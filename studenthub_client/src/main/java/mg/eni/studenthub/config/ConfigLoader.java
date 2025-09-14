package mg.eni.studenthub.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utilitaire centralisé pour charger la configuration de l'application
 * depuis le fichier {@code config.properties} situé dans le classpath (resources).
 *
 * <p>Cette classe charge automatiquement le fichier lors de la première utilisation
 * et met les valeurs en cache pour toutes les lectures suivantes.</p>
 *
 * <p>⚠️ Bonnes pratiques :
 * <ul>
 *   <li>Ne jamais stocker de secrets en clair (mot de passe DB, clés privées...)</li>
 *   <li>Utiliser des variables d’environnement ou un vault en production</li>
 *   <li>Définir des valeurs par défaut raisonnables dans le code si possible</li>
 * </ul>
 * </p>
 */
public final class ConfigLoader {

    /** Conteneur des propriétés chargées */
    private static final Properties props = new Properties();

    // Chargement statique du fichier dès la première utilisation
    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                System.err.println("[WARN] File config.properties not found in classpath.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load config.properties : " + e.getMessage());
        }
    }

    // Constructeur privé -> utilitaire statique uniquement
    private ConfigLoader() {}

    /**
     * Récupère la valeur associée à une clé donnée.
     *
     * @param key la clé de la propriété
     * @return la valeur de la propriété ou {@code null} si absente
     */
    public static String get(String key) {
        return props.getProperty(key);
    }

    /**
     * Récupère la valeur d'une clé donnée en tant qu'entier.
     *
     * @param key          la clé de la propriété
     * @param defaultValue valeur par défaut à retourner si la clé est absente ou invalide
     * @return l'entier correspondant à la valeur trouvée, ou {@code defaultValue} si erreur
     */
    public static int getInt(String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                System.err.printf("[WARN] Value '%s' for keys '%s' isn't a valid integer.%n", value, key);
            }
        }
        return defaultValue;
    }

    /**
     * Récupère la valeur d'une clé donnée avec valeur par défaut.
     *
     * @param key          la clé de la propriété
     * @param defaultValue valeur par défaut si la clé est absente
     * @return la valeur de la propriété, ou {@code defaultValue} si absente
     */
    public static String getOrDefault(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
