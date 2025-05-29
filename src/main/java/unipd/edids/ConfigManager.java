package unipd.edids;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//fix path relativi anche a jpackage!


public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private final List<ConfigObserver> observers = new ArrayList<>();
    private Dotenv dotenv;
    private Properties properties;
    private String configFilePath;

    private ConfigManager() {
        dotenv = Dotenv.load();
configFilePath = getEnv("CONFIG_FILE_PATH");
        properties = new Properties();
        loadProperties();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addObserver(ConfigObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String key, String value) {
        for (ConfigObserver observer : observers) {
            observer.onConfigChange(key, value);
        }
    }

public String getEnv(String key) {
    String value = dotenv.get(key);
    if (value == null) {
        logger.error("Configuration key {} not found.", key);
        throw new IllegalArgumentException("Environment variable " + key + " is not defined.");
    }
    return value;
}

public String getProperty(String key) {
    String value = properties.getProperty(key);
    if (value == null) {
        logger.error("Property key {} not found.", key);
        throw new IllegalArgumentException("Property " + key + " is not defined.");
    }
    return value;
}

    public void setProperty(String key, String value) {
        String oldValue = properties.getProperty(key); // Controlla il valore precedente
        properties.setProperty(key, value); // Aggiorna il valore in memoria

        // Notifica gli osservatori solo se il valore è cambiato
        if (!value.equals(oldValue)) {
            notifyObservers(key, value);
        }
    }

    public void saveProperties() {
        try (OutputStream output = new FileOutputStream(configFilePath)) {
            properties.store(output, null); // Salva tutte le modifiche in memoria nel file
            logger.info("Configuration saved to {}", configFilePath);
        } catch (IOException e) {
            logger.error("Failed to save properties to file", e);
        }
    }

}
