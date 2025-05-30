package unipd.edids.logicBusiness.managers;

import com.google.common.annotations.VisibleForTesting;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.observers.configObserver.ConfigObserver;

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
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            logger.error("Configuration file {} does not exist.", configFilePath);
            List<String> lines = null;
            lines = FileManager.readFile(getEnv("DEFAULT_CONFIG_FILE_PATH"));

            for (String line : lines) {
                FileManager.appendLineToSavingFile(configFilePath, line);

            }
        }

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

    public void loadProperties() {
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
        if (value == null || value.isBlank()) {
            logger.error("Configuration key {} not found.", key);
            throw new IllegalArgumentException("Environment variable " + key + " is not defined.");
        }
        return value;
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
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


    public String getConfigFilePath() {
        return configFilePath;
    }

    public void resetDefault(String apiKey) throws IOException {
        String defaultConfigPath = getEnv("DEFAULT_CONFIG_FILE_PATH");
        String configFilePath = getConfigFilePath();

        // Verifica se 'DEFAULT_CONFIG_FILE_PATH' esiste
        File defaultConfigFile = new File(defaultConfigPath);
        if (!defaultConfigFile.exists()) {
            logger.error("File di configurazione di default non trovato: {}", defaultConfigPath);
            throw new IOException("File di configurazione di default mancante o non accessibile: " + defaultConfigPath);
        }

        // Usa un file temporaneo per la scrittura
        File tempFile = new File(configFilePath + ".tmp");

        // Elimina il file corrente esistente
        FileManager.deleteFile(configFilePath);

        // Copia il contenuto del file di default nel file temporaneo
        List<String> lines = new ArrayList<>(FileManager.readFile(defaultConfigPath));
        String newApiLine = "api.key.file=" + apiKey;

        if (lines.removeIf(line -> line.startsWith("api.key.file="))) {
            lines.add(newApiLine);
        } else {
            lines.add(newApiLine);
        }

        for (String line : lines) {
            FileManager.appendLineToSavingFile(tempFile.getAbsolutePath(), line);
        }

        // Rinomina il file temporaneo in quello ufficiale
        if (!tempFile.renameTo(new File(configFilePath))) {
            throw new IOException("Impossibile rinominare il file temporaneo in: " + configFilePath);
        }

        // Ricarica la configurazione nel ConfigManager
        loadProperties();

    }
}
