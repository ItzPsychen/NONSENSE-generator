package unipd.edids;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private Dotenv dotenv;
    private Properties properties;
    private String configFilePath;

    private ConfigManager() {
        dotenv = Dotenv.load();
        configFilePath = dotenv.get("CONFIG_FILE_PATH", "src/main/resources/config.properties");
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
        return dotenv.get(key, defaultValue);
    }

    public String getConfig(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
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
