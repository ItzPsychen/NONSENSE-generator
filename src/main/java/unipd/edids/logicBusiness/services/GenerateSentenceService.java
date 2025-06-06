package unipd.edids.logicBusiness.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.strategies.structureStrategies.*;
import unipd.edids.logicBusiness.strategies.wordSelectionStrategies.NewWordStrategy;
import unipd.edids.logicBusiness.strategies.wordSelectionStrategies.OriginalWordStrategy;
import unipd.edids.logicBusiness.strategies.wordSelectionStrategies.WordSelectionStrategy;

import java.util.Iterator;
import java.util.regex.Pattern;


/**
 * Service responsible for generating sentences based on configurable strategies for structure and word selection.
 *
 * <p> Responsibilities:
 * - Generates sentences by applying configurable structure generation and word selection strategies.
 * - Handles recursive sentence structures and resolves placeholders with appropriate words (nouns, verbs, adjectives).
 * - Observes configuration changes and adapts behavior dynamically.
 *
 * <p> Design Pattern:
 * - Strategy Pattern: Uses structure and word selection strategies to modularize and extend behavior.
 */
public class GenerateSentenceService {
    /**
     * Provides logging functionality for the GenerateSentenceService class.
     * Uses the LogManager to facilitate logging operations.
     */
    private static final Logger logger = LogManager.getLogger(GenerateSentenceService.class);
    /**
     * Represents the tagging label used to identify sentence elements in the sentence being generated.
     */
    private static final String SENTENCE_TAG = "[sentence]";
    /**
     * Represents the tagging label used to identify noun elements in the sentence being generated.
     */
    private static final String NOUN_TAG = "[noun]";
    /**
     * Represents the tagging label used to identify verb elements in the sentence being generated.
     */
    private static final String VERB_TAG = "[verb]";
    /**
     * Represents the tagging label used to identify adjective elements in the sentence being generated.
     */
    private static final String ADJECTIVE_TAG = "[adjective]";
    /**
     * Represents the sentence structure strategy used by the service.
     */
    private StructureSentenceStrategy structureSentenceStrategy;
    /**
     * Defines the strategy for selecting and populating words in dynamically generated sentences.
     */
    private WordSelectionStrategy wordSelectionStrategy;
    /**
     * Represents the current sentence being processed or generated by the GenerateSentenceService.
     */
    private Sentence currentSentence = new Sentence(); // Inizializzato direttamente qui

    /**
     * Generates a new sentence based on the configured strategies,
     * applying specific rules and transformations.
     * The method performs the following steps:
     * - Creates a new `Sentence` instance to reset the current state.
     * - Configures the structure of the sentence using the provided strategy.
     * - Populates the structure with words by replacing placeholders.
     * - Applies transformations such as capitalization of the first letter.
     * - Logs intermediate and final stages of the sentence generation process.
     *
     * @return A fully constructed `Sentence` object with text and structure.
     */
    public Sentence generateSentence() {
        logger.info("Starting sentence generation process.");
        currentSentence = new Sentence();

        logger.info("Generating sentence with strategy: {}", structureSentenceStrategy.getClass().getSimpleName());

        // Configura la struttura della frase
        logger.info("Configuring sentence structure...");
        configureSentenceStructure();

        logger.info("Initial Sentence Structure: {}", currentSentence.getStructure());

        // Popola i placeholder con le parole
        logger.info("Word Selection Strategy: {}", wordSelectionStrategy.getClass().getSimpleName());
        logger.info("Populating sentence placeholders with selected strategy...");
        wordSelectionStrategy.populateWords(currentSentence);

        // Sostituisci i segnaposto e applica ulteriori trasformazioni
        logger.info("Replacing placeholders with actual words...");
        replacePlaceholders();

        logger.debug("Capitalizing the first letter of the generated sentence...");
        capitalizeFirstLetter();

        logger.info("Final Sentence: {}", currentSentence.getSentence());
        return currentSentence;
    }

    /**
     * Configures and adjusts the structure of the current sentence based on the defined strategies and configurations.
     * This method defines the sentence structure, applies recursive or static adjustments, and ensures a proper format.
     */
    private void configureSentenceStructure() {
        logger.info("Starting sentence structure configuration...");
        currentSentence.setStructure(structureSentenceStrategy.generateSentenceStructure());

        // Estrae impostazioni di configurazione
        boolean allowRecursive = "true".equals(ConfigManager.getInstance().getProperty("allow.recursive.sentences"));
        logger.info("Recursive mode enabled: {}", allowRecursive);

        int maxRecursionLevel = Integer.parseInt(ConfigManager.getInstance().getProperty("max.recursion.level"));
        logger.info("Max recursion level: {}", maxRecursionLevel);

        if (allowRecursive) {
            logger.info("Resolving recursive template structure...");
            String resolvedTemplate = resolveTemplate(currentSentence.getStructure().toString(), 0, maxRecursionLevel);
            currentSentence.setStructure(new StringBuilder(resolvedTemplate));
        } else if (currentSentence.getStructure().toString().contains(SENTENCE_TAG)) {
            logger.info("Adjusting sentence structure for non-recursive mode...");
            String adjustedStructure = currentSentence.getStructure().toString().replaceAll(Pattern.quote(SENTENCE_TAG), NOUN_TAG);
            currentSentence.setStructure(new StringBuilder(adjustedStructure));
        }
    }

    /**
     * Replaces placeholders within the structure of the current sentence with the appropriate nouns, verbs,
     * and adjectives provided by iterators. Each placeholder ([noun], [verb], or [adjective]) is replaced sequentially with available words
     * from their respective lists, updating the sentence structure.
     */
    // Sostituisce i segnaposti [noun], [verb], [adjective]
    private void replacePlaceholders() {
        logger.info("Starting placeholder replacement...");
        String result = currentSentence.getStructure().toString();
        Iterator<String> nounsIterator = currentSentence.getNouns().iterator();
        Iterator<String> verbsIterator = currentSentence.getVerbs().iterator();
        Iterator<String> adjectiveIterator = currentSentence.getAdjectives().iterator();

        while (result.contains(NOUN_TAG) && nounsIterator.hasNext()) {
            result = result.replaceFirst(Pattern.quote(NOUN_TAG), nounsIterator.next());
            logger.debug("Replacing placeholder [noun] with next word from nouns list.");
        }
        while (result.contains(VERB_TAG) && verbsIterator.hasNext()) {
            result = result.replaceFirst(Pattern.quote(VERB_TAG), verbsIterator.next());
            logger.debug("Replacing placeholder [verb] with next word from verbs list.");
        }
        while (result.contains(ADJECTIVE_TAG) && adjectiveIterator.hasNext()) {
            result = result.replaceFirst(Pattern.quote(ADJECTIVE_TAG), adjectiveIterator.next());
            logger.debug("Replacing placeholder [adjective] with next word from adjectives list.");
        }

        logger.info("Sentence after placeholders replacement: {}", result);
        currentSentence.setSentence(new StringBuilder(result));

    }

    /**
     * Capitalizes the first letter of the generated sentence string.
     */
    // Capitalizza la prima lettera della frase generata
    private void capitalizeFirstLetter() {
        StringBuilder sentence = currentSentence.getSentence();
        if (!sentence.isEmpty()) {
            sentence.setCharAt(0, Character.toUpperCase(sentence.charAt(0)));
        }
    }

    /**
     * Resolves a template string by recursively replacing placeholder tags with appropriate content.
     *
     * @param template          The initial template string containing placeholders to be replaced.
     * @param depth             Current recursion depth to monitor and limit the traversal.
     * @param maxRecursionDepth The maximum allowed recursion depth for resolving templates.
     * @return The resolved template string with placeholders replaced.
     */
    private String resolveTemplate(String template, int depth, int maxRecursionDepth) {
        if (depth > maxRecursionDepth) {
            logger.warn("Max recursion depth exceeded at depth {}. Returning default tag.", depth);
            return NOUN_TAG;
        }
        while (template.contains(SENTENCE_TAG)) {
            template = template.replaceFirst(Pattern.quote(SENTENCE_TAG), resolveTemplate(structureSentenceStrategy.generateSentenceStructure().toString(), depth + 1, maxRecursionDepth));
        }
        return template;
    }


    /**
     * Sets the strategy for generating the sentence structure based on the specified strategy type.
     *
     * @param strategy      The type of strategy to be set. Must be one of the values defined in StrategyType.
     * @param inputSentence The sentence whose structure may be used depending on the strategy type.
     * @param selStructure  The selected structure to use, applicable if the strategy type is SELECTED.
     * @throws IllegalArgumentException if the strategy type is null or invalid.
     */
    public void setStructureSentenceStrategy(StrategyType strategy, Sentence inputSentence, String selStructure) {
        if (strategy == null) {
            String errorMessage = "Strategy cannot be null.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        logger.info("Setting Structure Sentence Strategy: {}", strategy);
        switch (strategy) {
            case RANDOM:
                this.structureSentenceStrategy = new RandomStructureStrategy();
                logger.info("Applied Random Structure Strategy.");
                break;
            case SAME:
                this.structureSentenceStrategy = new SameAsAnalyzedStructureStrategy(inputSentence);
                logger.info("Applied Same-As-Analyzed Structure Strategy.");
                break;
            case SELECTED:
                this.structureSentenceStrategy = new SelectedStructureStrategy(selStructure);
                logger.info("Applied Selected Structure Strategy for structure: {}", selStructure);

                break;
            default:
                String errorMessage = "Invalid strategy: " + strategy;
                logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Validates the input sentence and its associated parameters, ensuring they meet specified conditions.
     *
     * @param inputSentence the sentence to validate; must not be null when specific conditions are met.
     * @param newWords      a boolean flag that specifies if new words should be considered in the validation.
     * @param strategy      the strategy type to apply during validation; options include RANDOM, SAME, and SELECTED.
     * @throws IllegalArgumentException if the input sentence is null and the conditions are not satisfied.
     */
    public void validateInput(Sentence inputSentence, boolean newWords, StrategyType strategy) {
        if (inputSentence == null) {
            if (!newWords || strategy.equals(StrategyType.SAME)) {
                String errorMessage = "Input sentence cannot be null. Please analyze a sentence first or enable the 'new words' option while selecting a valid structure (Random or Selected).";
                logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    /**
     * Configures the word selection strategy for generating sentence components based on the provided parameters.
     *
     * @param newWords      a boolean flag indicating whether to use a strategy for generating new words or retaining original words.
     * @param inputSentence the sentence object to be used as the basis for the original word strategy, if applicable.
     */
    public void configureWordStrategy(boolean newWords, Sentence inputSentence) {
        String strategyType = newWords ? "NewWordStrategy" : "OriginalWordStrategy";
        this.wordSelectionStrategy = newWords ? new NewWordStrategy() : new OriginalWordStrategy(inputSentence);
        logger.info("Configuring Word Selection Strategy: {} with associated input sentence: {}", strategyType, inputSentence);
    }
}
