package mg.eni.studenthub.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.*;

public class LoggerConfig {
    public static void setup() {
        Logger rootLogger = Logger.getLogger("");

        try {
            Files.createDirectories(Paths.get("logs"));

            Handler fileHandler = new FileHandler("logs/studenthub.log", true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();

            fileHandler.setFormatter(simpleFormatter);
            fileHandler.setLevel(Level.SEVERE);

            rootLogger.setLevel(Level.SEVERE);
            rootLogger.addHandler(fileHandler);

            for (Handler handler: rootLogger.getHandlers()) {
                if (handler instanceof ConsoleHandler) {
                    handler.setLevel(Level.ALL);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
