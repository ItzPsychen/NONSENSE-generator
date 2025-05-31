package unipd.edids.logicBusiness.entities;

import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;
import unipd.edids.logicBusiness.observers.configObserver.ConfigObserver;
import unipd.edids.logicBusiness.strategies.tenseStrategies.FutureTenseStrategy;
import unipd.edids.logicBusiness.strategies.tenseStrategies.PresentTenseStrategy;
import unipd.edids.logicBusiness.strategies.tenseStrategies.TenseStrategy;

import java.util.Random;

/**
 * The Verb class is responsible for managing and accessing a list of verbs
 * loaded from a file, with support for dynamic updates to the file configuration.
 *
 * <p>
 * Responsibilities:
 * - Manages a list of verbs loaded from an external file.
 * - Monitors configuration updates to adapt file references dynamically.
 * - Supports verb conjugation via a pluggable strategy pattern.
 * - Provides a Singleton instance to ensure a single point of access.
 *
 * <p>
 * Design Patterns:
 * - Singleton: Ensures only one instance of the class exists.
 * - Strategy: Allows dynamic changes in the conjugation logic via TenseStrategy.
 * - Observer: Responds to configuration changes for dynamic file management.
 */
public class Verb extends Word implements ConfigObserver {

    /**
     * Singleton instance of the Verb class shared across the application.
     */
    private static Verb instance;
    /**
     * Strategy interface for verb conjugation logic.
     * Allows dynamic changes to the conjugation process.
     */
    private TenseStrategy tenseStrategy;

    /**
     * Private constructor for the Verb class.
     * Initializes the Verb instance with filePath and observes configuration and file updates.
     */
    private Verb() {
        super(ConfigManager.getInstance().getProperty("verb.file"));
        ConfigManager.getInstance().addObserver(this);
        FileManager.addObserver(this);
    }

    /**
     * Provides a globally accessible instance of the Verb class.
     *
     * @return the singleton instance of the Verb class
     */
    public static Verb getInstance() {
        if (instance == null) {
            instance = new Verb();
        }
        return instance;
    }


    /**
     * Handles configuration changes for the Verb class, specifically updates the file path for loading adjectives.
     *
     * @param key The configuration property that has changed.
     * @param value The new value of the changed configuration property.
     */
    @Override
    public void onConfigChange(String key, String value) {
        if ("verb.file".equals(key)) {
            this.filePath = value;
            loadWords(this.filePath);
        }
    }

    /**
     * Returns a random verb from the list of verbs and applies
     * the currently set conjugation strategy.
     *
     * @return A conjugated verb chosen randomly from the list,
     * or "undefined" if the list is empty.
     */
    public String getRandomWord() {
        if (words.isEmpty()) return "undefined";
        Random random = new Random();
        return conjugate(words.get(random.nextInt(words.size())));
    }

    /**
     * Conjugates the provided verb using the current tense strategy.
     *
     * @param verb The verb to be conjugated.
     * @return The conjugated form of the provided verb.
     */
    public String conjugate(String verb) {
        if (this.tenseStrategy == null) {
            configureVerbTense(false);
        }
        return this.tenseStrategy.conjugate(verb);
    }

    /**
     * Retrieves the currently assigned TenseStrategy for conjugating verbs.
     *
     * @return the active TenseStrategy used for verb conjugation.
     */
    public TenseStrategy getTenseStrategy() {
        return tenseStrategy;
    }

    /**
     * Configures the verb tense strategy for the Verb class.
     * Adjusts between future tense and present tense strategies
     * based on the input parameter.
     *
     * @param futureTense A boolean value indicating if future tense
     *                    should be applied (true for future, false for present).
     */
    public void configureVerbTense(boolean futureTense) {
        if (futureTense) {
            this.tenseStrategy = new FutureTenseStrategy();
        } else {
            this.tenseStrategy = new PresentTenseStrategy();
        }
    }
}

