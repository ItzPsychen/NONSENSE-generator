package unipd.edids.logicBusiness.entities;

import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;
import unipd.edids.logicBusiness.observers.configObserver.ConfigObserver;

/**
 * The Adjective class is responsible for managing and accessing a list of adjectives
 * loaded from a file, with support for dynamic updates to the file configuration.
 *
 * <p>Responsibilities:
 * - Implements the Singleton design pattern to ensure a single instance exists throughout the application.
 * - Observes configuration changes through the ConfigObserver interface to update file paths dynamically.
 * - Observes file changes through the inherited behavior from the Word class to reload adjectives when the file is modified.
 * - Manages the lifecycle and state of adjectives, enabling retrieval of adjectives dynamically.
 *
 * <p>Design Pattern:
 * - Singleton Pattern: Ensures that only one instance of the Adjective class is created and provides global access to it.
 * - Observer Pattern: Observes configuration and file-based changes to dynamically adapt the adjective list.
 */
public class Adjective extends Word implements ConfigObserver {

    /**
     * Singleton instance of the Adjective class shared across the application.
     */
    private static Adjective instance;


    /**
     * Private constructor for the Adjective class.
     * Initializes the Adjective instance with filePath and observes configuration and file updates.
     */
    private Adjective() {
        super(ConfigManager.getInstance().getProperty("adjective.file"));
        ConfigManager.getInstance().addObserver(this);
        FileManager.addObserver(this);
    }


    /**
     * Provides a globally accessible singleton instance of the Adjective class.
     *
     * @return the singleton instance of the Adjective class
     */
    public static Adjective getInstance() {
        if (instance == null) {
            instance = new Adjective();
        }
        return instance;
    }


    /**
     * Handles configuration changes for the Adjective class, specifically updates the file path for loading adjectives.
     *
     * @param key The configuration property that has changed.
     * @param value The new value associated with the specified configuration property.
     */
    @Override
    public void onConfigChange(String key, String value) {
        if ("adjective.file".equals(key)) {
            this.filePath = value;
            loadWords(this.filePath);
        }
    }

}