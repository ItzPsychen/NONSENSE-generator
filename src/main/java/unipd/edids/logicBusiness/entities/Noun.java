package unipd.edids.logicBusiness.entities;

import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;
import unipd.edids.logicBusiness.observers.configObserver.ConfigObserver;

/**
 * The Noun class is responsible for managing and accessing a list of nouns
 * loaded from a file, with support for dynamic updates to the file configuration.
 *
 * <p> Responsibilities:
 * - Implements the Singleton design pattern to ensure a single instance of the class.
 * - Observes configuration changes related to noun file management and updates accordingly.
 * - Extends the "Word" class for file-based noun word handling.
 *
 * <p> Design Patterns:
 * - Singleton: Ensures a single instance of the Noun class.
 * - Observer: Observes configuration changes via "ConfigObserver".
 */
public class Noun extends Word implements ConfigObserver {

    /**
     * Singleton instance of the Noun class shared across the application.
     */
    private static Noun instance;

    /**
     * Private constructor for the Noun class.
     * Initializes the Noun instance with filePath and observes configuration and file updates.
     */
    private Noun() {
        super(ConfigManager.getInstance().getProperty("noun.file"));
        ConfigManager.getInstance().addObserver(this);
        FileManager.addObserver(this);
    }

    /**
     * Provides a globally accessible instance of the Noun class.
     *
     * @return the singleton instance of the Noun class
     */
    public static Noun getInstance() {
        if (instance == null) {
            instance = new Noun();
        }
        return instance;
    }

    /**
     * Handles configuration changes for the Noun class, specifically updates the file path for loading adjectives.
     *
     * @param key The configuration property that has changed.
     * @param value The new value of the configuration property.
     */
    @Override
    public void onConfigChange(String key, String value) {
        if ("noun.file".equals(key)) {
            this.filePath = value;
            loadWords(this.filePath);
        }
    }
}