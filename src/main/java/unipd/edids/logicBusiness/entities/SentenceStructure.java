package unipd.edids.logicBusiness.entities;

import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;
import unipd.edids.logicBusiness.observers.configObserver.ConfigObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages sentence structures for generating sentences and observes configuration changes.
 *
 * <p>Responsibilities:
 * - Stores and manages a collection of sentence structures.
 * - Provides a random sentence structure.
 * - Observes configuration changes and reloads sentence structures dynamically.
 *
 * <p>Design Pattern:
 * - Singleton Pattern: Ensures only one instance of the class exists.
 * - Observer Pattern: Implements ConfigObserver to listen for configuration updates.
 */
public class SentenceStructure implements ConfigObserver {
    /**
     * Default sentence structure used when no custom structures are provided or available.
     *
     * Format: "[NOUN] [VERB] [NOUN]"
     */
    private static final String DEFAULT_STRUCTURE = "[NOUN] [VERB] [NOUN]";
    /**
     * Single instance of the SentenceStructure class.
     * Implements Singleton design pattern to ensure one global access point.
     */
    private static SentenceStructure instance; // Single instance of the class
    /**
     * List of sentence structures used for generating sentences.
     */
    private List<String> structures;


    /**
     * Manages sentence structures for generating sentences and observes configuration changes.
     *
     * <p>Responsibilities:
     * - Stores and manages a collection of sentence structures.
     * - Provides a random sentence structure.
     * - Observes configuration changes and reloads sentence structures dynamically.
     *
     * <p>Design Pattern:
     * - Singleton Pattern: Ensures only one instance of the class exists.
     * - Observer Pattern: Implements ConfigObserver to listen for configuration updates.
     */
    // Private constructor to prevent direct instantiation
    private SentenceStructure() {
        structures = new ArrayList<>();
        ConfigManager.getInstance().addObserver(this);
        loadStructures();
    }

    /**
     * Public static method to retrieve the unique instance of the SentenceStructure class.
     * Implements the Singleton design pattern.
     *
     * @return the single instance of SentenceStructure
     */
    // Public static method to get the unique instance
    public static SentenceStructure getInstance() {
        if (instance == null) {
            instance = new SentenceStructure();
        }
        return instance;
    }

    /**
     * Loads sentence structures from a file specified in the configuration.
     *
     * Utilizes FileManager's readFile method to read non-empty lines from the file path
     * retrieved using ConfigManager. Updates the internal list of sentence structures
     * with the file's contents.
     */
    private void loadStructures() {
        structures = FileManager.readFile(ConfigManager.getInstance().getProperty("sentence.structures"));
    }

    /**
     * Retrieves a random sentence structure from the available structures.
     * Returns a default structure if no structures are defined.
     *
     * @return a random structure from the list of sentence structures,
     * or the default structure if the list is empty.
     */
    public String getRandomStructure() {
        if (structures.isEmpty()) return DEFAULT_STRUCTURE;
        Random random = new Random();
        return structures.get(random.nextInt(structures.size()));
    }

    /**
     * Handles configuration changes specific to the sentence structures.
     *
     * @param key The configuration property that has been updated.
     * @param value The new value of the configuration property.
     */
    @Override
    public void onConfigChange(String key, String value) {
        if ("sentence.structures".equals(key)) {
            loadStructures();
        }
    }

    /**
     * Retrieves the list of structures.
     *
     * @return A list of strings representing the structures.
     */
    public List<String> getStructures() {
        return structures;
    }
}