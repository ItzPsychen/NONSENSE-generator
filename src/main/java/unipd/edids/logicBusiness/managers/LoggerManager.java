package unipd.edids.logicBusiness.managers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the creation and provision of logger instances to ensure consistent logging functionality across the application.
 *
 * <p>Responsibilities:</p>
 * - Provides a centralized mechanism for obtaining logger instances.
 * - Ensures only one instance of the logger manager exists throughout the application's lifecycle.
 *
 * <p>Design Pattern:</p>
 * - Implements the Singleton design pattern to maintain a single, globally accessible LoggerManager instance.
 */
public class LoggerManager {

    /**
     * Singleton instance of the LoggerManager.
     * Ensures a single, globally accessible logger manager throughout the application's lifecycle.
     */
    private static LoggerManager instance;

    /**
     * Manages the creation and provision of logger instances to ensure consistent logging functionality across the application.
     *
     * <p>Responsibilities:</p>
     * - Provides a centralized mechanism for obtaining logger instances.
     * - Ensures only one instance of the logger manager exists throughout the application's lifecycle.
     *
     * <p>Design Pattern:</p>
     * - Implements the Singleton design pattern to maintain a single, globally accessible LoggerManager instance.
     */
    private LoggerManager() {
        // Costruttore privato
    }

    /**
     * Retrieves the singleton instance of the LoggerManager class, ensuring only one instance exists.
     *
     * @return The single instance of LoggerManager.
     */
    public static synchronized LoggerManager getInstance() {
        if (instance == null) {
            instance = new LoggerManager();
        }
        return instance;
    }

    /**
     * Retrieves a logger instance specific to the provided class.
     *
     * @param clazz The class object for which the logger is being requested.
     * @return A logger instance associated with the specified class.
     */
    public Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}
