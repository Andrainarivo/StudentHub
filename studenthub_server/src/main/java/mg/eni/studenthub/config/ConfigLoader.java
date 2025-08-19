package mg.eni.studenthub.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties props = new Properties();

    static {
        try (
            InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")
        ) {
            if (input != null) {
                props.load(input);
            }
            
        } catch (IOException e) {
            System.err.println(" Impossible de charger le fichier config.properties ." + e.getMessage());    
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static int getInt(String key, int defaulValue) {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (NumberFormatException e) {
            return defaulValue;
        }
    }

}
