package mg.eni.studenthub.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.*;

/**
 * Utility class for configuring the global application logger.
 * <p>
 * This class sets up a {@link java.util.logging.FileHandler} that writes
 * log messages to {@code logs/studenthub.log}. It ensures logs are stored
 * persistently while restricting console logs to only severe messages.
 * </p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Creates a {@code logs/} directory if it does not exist.</li>
 *   <li>Logs everything ({@link Level#ALL}) to a file with a simple text format.</li>
 *   <li>Restricts console output to {@link Level#SEVERE} only.</li>
 * </ul>
 *
 * <p>
 * Call {@link #setup()} once, typically at the application startup,
 * before using any {@link Logger} instances.
 * </p>
 *
 * <pre>
 * Example usage:
 *     LoggerConfig.setup();
 *     Logger logger = Logger.getLogger(MyClass.class.getName());
 *     logger.info("Application started!");
 * </pre>
 */
public class LoggerConfig {

    /**
     * Configures the root logger for the application.
     * <p>
     * - Creates the {@code logs} directory if missing. <br>
     * - Adds a file handler writing to {@code logs/studenthub.log}. <br>
     * - Restricts console output to SEVERE logs only. <br>
     * </p>
     */
    public static void setup() {
        Logger rootLogger = Logger.getLogger("");

        try {
            // Ensure the log directory exists
            Files.createDirectories(Paths.get("logs"));

            // File handler logs everything to logs/studenthub.log
            Handler fileHandler = new FileHandler("logs/studenthub.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.SEVERE);

            // Root logger setup
            rootLogger.setLevel(Level.SEVERE);
            rootLogger.addHandler(fileHandler);

            // Restrict console logs to SEVERE only
            for (Handler handler : rootLogger.getHandlers()) {
                if (handler instanceof ConsoleHandler) {
                    handler.setLevel(Level.ALL);
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Failed to initialize logging configuration: " + e.getMessage());
        }
    }
}
