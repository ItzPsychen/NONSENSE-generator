package unipd.edids;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Dotenv dotenv;
    private Properties properties;
    private String configFilePath;

    private ConfigManager() {
        dotenv = Dotenv.load();
        configFilePath = getEnv("CONFIG_FILE_PATH", "src/main/resources/config.properties");
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

    public String getEnv(String key, String defaultValue) {
        String value = dotenv.get(key);
        if (value == null) {
            logger.warn("Configuration key {} not found. Using default: {}", key, defaultValue);
            return defaultValue;
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property key {} not found. Using default: {}", key, defaultValue);
            return defaultValue;
        }
        return value;
    }

    public void setConfig(String key, String value) {
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(configFilePath)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
