package unipd.edids.logicBusiness.managers;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.exceptions.MissingApiKeyException;
import unipd.edids.logicBusiness.observers.configObserver.ConfigObserver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * The ConfigManager class is responsible for managing configuration properties in an application.
 *
 * <p>Responsibilities:
 * - Provides methods to load, save, and manage application configuration properties.
 * - Ensures configuration files exist and handles default file generation if needed.
 * - Observes and notifies registered observers about configuration changes.
 *
 * <p>Design Pattern:
 * - Singleton Pattern: Ensures only one instance of ConfigManager is used throughout the application.
 * - Observer Pattern: Allows registered observers to react dynamically to changes in configuration properties.
 */
public class ConfigManager {
    /**
     * Logger instance used for logging messages within the ConfigManager class.
     * <p>
     * Responsibilities:
     * - Provides a centralized way to log information, warnings, and errors for ConfigManager.
     * - Facilitates consistent logging using Log4j2.
     */
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    /**
     * Represents the default configuration file path key used to locate the primary configuration file.
     */
    private static final String DEFAULT_CONFIG_KEY = "DEFAULT_CONFIG_FILE_PATH";
    /**
     * File path property for storing the API key.
     */
    private static final String API_KEY_PROPERTY = "api.key.file";
    /**
     * Singleton instance of the ConfigManager class.
     * Ensures a single, globally accessible ConfigManager throughout the application's lifecycle.
     */
    private static ConfigManager instance;
    /**
     * List of registered ConfigObserver instances monitoring configuration changes.
     * <p>
     * Responsibilities:
     * - Maintain observers monitoring configuration updates.
     * - Enable the Observer design pattern for dynamic notifications.
     * <p>
     * Design Pattern:
     * - Observer Pattern: Acts as the Subject, notifying observers of configuration changes.
     */
    private final List<ConfigObserver> observers = new ArrayList<>();
    /**
     * Provides access to environment variables using the Dotenv library.
     *
     * <p>Responsibilities:
     * - Manages and retrieves environment variables from a `.env` file.
     * - Facilitates secure and centralized handling of environment configurations.
     */
    private final Dotenv dotenv;
    /**
     * Maintains configuration properties for the application, enabling configuration management and dynamic updates.
     */
    private final Properties properties = new Properties();
    /**
     * Path to the configuration file used for storing and retrieving application settings.
     */
    private final String configFilePath;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ConfigManager() {
        dotenv = Dotenv.load();
        configFilePath = getEnv("CONFIG_FILE_PATH");
        ensureConfigFileExists();
        loadProperties();
    }

    /**
     * Retrieves the singleton instance of the ConfigManager class, ensuring
     * only one instance exists and provides controlled access.
     *
     * @return The single instance of ConfigManager.
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            logger.debug("Creating new ConfigManager instance");
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Ensures that the configuration file exists. If the file is not present, a default configuration file is
     * created by invoking {@code createDefaultConfigFile()}.
     *
     * <p>Logs a warning if the configuration file does not exist and a debug message indicating the use of a
     * default configuration file.
     */
    private void ensureConfigFileExists() {
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            logger.warn("Configuration file {} does not exist.", configFilePath);
            logger.debug("Using default configuration file at: {}", configFilePath);
            createDefaultConfigFile();
        }
    }

    /**
     * Creates a default configuration file by reading from the default configuration path and appending its contents
     * to the application's configuration file.
     *
     * <p>If the default configuration file does not exist, this method logs an error and throws a runtime exception.
     */
    private void createDefaultConfigFile() {
        String defaultConfigPath = getEnv(DEFAULT_CONFIG_KEY);
        File defaultConfigFile = new File(defaultConfigPath);

        if (!defaultConfigFile.exists()) {
            logger.error("Default configuration file {} does not exist.", defaultConfigPath);
            throw new RuntimeException("Default configuration file is missing or inaccessible: " + defaultConfigPath);
        }

        List<String> defaultConfigLines = FileManager.readFile(defaultConfigPath);
        for (String line : defaultConfigLines) {
            FileManager.appendLineToSavingFile(configFilePath, line);
        }
    }

    /**
     * Loads properties from a configuration file into memory.
     *
     * <p>Attempts to read the specified configuration file defined within the class's context.
     * Throws a {@link RuntimeException} if any I/O errors occur during the process.
     */
    public void loadProperties() {
        try (InputStream input = new FileInputStream(configFilePath)) {
            logger.debug("Loading properties from file: {}", configFilePath);
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from file: " + configFilePath, e);
        }
    }

    /**
     * Adds an observer to the list of observers for monitoring configuration changes.
     *
     * @param observer The ConfigObserver to register for configuration updates.
     */
    public void addObserver(ConfigObserver observer) {
        observers.add(observer);
    }

    /**
     * Notifies registered observers of a configuration change if the new value differs from the previous value.
     *
     * @param key      The identifier for the property that has been modified.
     * @param oldValue The previous value of the property.
     * @param newValue The updated value of the property.
     */
    private void notifyObserversIfNeeded(String key, String oldValue,String newValue) {
        if (!newValue.equals(oldValue)) {
            logger.debug("Property '{}' changed from '{}' to '{}', notifying observers", key, oldValue, newValue);
            for (ConfigObserver observer : observers) {
                observer.onConfigChange(key, newValue);
            }
        }
    }

    /**
     * Retrieves the value of the specified environment variable.
     * Throws an exception if the variable is not defined or is blank.
     *
     * @param key The name of the environment variable to retrieve.
     * @return The value of the environment variable as a String.
     * @throws IllegalArgumentException if the environment variable is not defined or is blank.
     */
    public String getEnv(String key) {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            logger.error("Environment variable {} is not defined.", key);
            throw new IllegalArgumentException("Environment variable " + key + " is not defined.");
        }
        return value;
    }

    /**
     * Retrieves the value of a property associated with the specified key.
     * Throws an exception if the key is not defined.
     *
     * @param key the key for which the property value is to be retrieved
     * @return the value of the property associated with the specified key
     * @throws IllegalArgumentException if the property key is not defined or has a blank value
     */
    public String getProperty(String key) {
        if (key == null || key.isBlank()) {
            logger.error("Property key {} is not defined.", key);
            throw new IllegalArgumentException("Property key " + key + " is not defined.");
        }
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            logger.error("Property {} is missing or empty in configuration.", key);
            if (API_KEY_PROPERTY.equals(key)) {
                throw new MissingApiKeyException();
            } else {
                throw new IllegalArgumentException("Property " + key + " is not defined.");
            }
        }
        return value;
    }

    /**
     * Updates or adds a property identified by the specified key and notifies observers if the value changes.
     *
     * @param key   The unique identifier for the property being set.
     * @param newValue The new value to assign to the specified property.
     */
    public void setProperty(String key, String newValue) {
        logger.debug("Setting property '{}' to '{}'", key, newValue);
        String oldValue = properties.getProperty(key);
        properties.setProperty(key, newValue);
        notifyObserversIfNeeded(key, oldValue, newValue);
    }

    /**
     * Saves the current properties to a configuration file.
     *
     * <p>This method writes the contents of the application's properties
     * to the specified configuration file path. If the operation is
     * successful, a log entry is created. In case of an error during
     * the file saving process, the issue is logged.
     */
    public void saveProperties() {
        try (OutputStream output = new FileOutputStream(configFilePath)) {
            properties.store(output, null);
            logger.info("Configuration saved to {}", configFilePath);
        } catch (IOException e) {
            logger.error("Failed to save properties to file: {}", configFilePath, e);
        }
    }

    /**
     * Resets the configuration to its default state using the specified default configuration file.
     *
     * <p>This method retrieves the current API key and the default configuration file path.
     * If the default configuration file does not exist, an error is logged and an {@link IOException} is thrown.
     * It then invokes a helper method to reset the configuration from the default file and updates the API key.
     *
     * @throws IOException If the default configuration file is missing or inaccessible.
     */
    public void resetDefault() throws IOException {
        String oldApiKey;
        try{
            oldApiKey = getProperty(API_KEY_PROPERTY);
        } catch (MissingApiKeyException e) {
            logger.warn("Trying to reset: API key property is not defined.");
            oldApiKey = "";
        }
        String defaultConfigPath = getEnv(DEFAULT_CONFIG_KEY);
        File defaultConfigFile = new File(defaultConfigPath);

        if (!defaultConfigFile.exists()) {
            logger.error("Trying to reset: Default configuration file {} does not exist.", defaultConfigPath);
            throw new IOException("Default configuration file is missing or inaccessible: " + defaultConfigPath);
        }

        resetConfigFromDefault(defaultConfigPath, oldApiKey);
    }

    /**
     * Resets the configuration to its default state using a specified configuration file path.
     *
     * @param defaultConfigPath The file path to the default configuration file.
     * @param newApiKey         The new API key to replace the existing API key.
     *                          If null or blank, the existing API key is not replaced.
     */
    private void resetConfigFromDefault(String defaultConfigPath, String newApiKey) {
        logger.info("Resetting configuration from default path: {}", defaultConfigPath);
        List<String> lines = FileManager.readFile(defaultConfigPath);

        // Update properties one at a time
        for (String line : lines) {
            if (!line.isBlank() && line.contains("=")) {
                String[] keyValue = line.split("=", 2);
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                // Use setProperty to update each property
                setProperty(key, value);
            }
        }

        // Update the specific property for an API key
        if (newApiKey != null && !newApiKey.isBlank()) {
            setProperty(API_KEY_PROPERTY, newApiKey);
        }

        // Save property changes to a configuration file
        saveProperties();
    }
}