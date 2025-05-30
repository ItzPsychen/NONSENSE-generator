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
    private static final Logger logger = LogManager.getLogger(GenerateSentenceService.class);
    private static final String SENTENCE_TAG = "[sentence]";
    private final String NOUN_TAG = "[noun]";
private final String VERB_TAG = "[verb]";
private final String ADJECTIVE_TAG = "[adjective]";
    private StructureSentenceStrategy structureSentenceStrategy;
    private WordSelectionStrategy wordSelectionStrategy;
    private Sentence currentSentence = new Sentence(); // Inizializzato direttamente qui

    public Sentence generateSentence() {
        // Ricrea la frase corrente prima di ogni generazione.
        currentSentence = new Sentence();

        logger.info("Generating sentence with strategy: {}", structureSentenceStrategy.getClass().getSimpleName());

        // Configura la struttura della frase
        configureSentenceStructure();

        logger.info("Initial Sentence Structure: {}", currentSentence.getStructure());

        // Popola i placeholder con le parole
        logger.info("Word Selection Strategy: {}", wordSelectionStrategy.getClass().getSimpleName());
        wordSelectionStrategy.populateWords(currentSentence);

        // Sostituisci i segnaposto e applica ulteriori trasformazioni
        replacePlaceholders();
        capitalizeFirstLetter();

        logger.info("Final Sentence: {}", currentSentence.getSentence());
        return currentSentence;
    }

    // Estrae e configura la struttura della frase
    private void configureSentenceStructure() {
        currentSentence.setStructure(structureSentenceStrategy.generateSentenceStructure());

        // Estrae impostazioni di configurazione
        boolean allowRecursive = "true".equals(ConfigManager.getInstance().getProperty("allow.recursive.sentences"));
        int maxRecursionLevel = Integer.parseInt(ConfigManager.getInstance().getProperty("max.recursion.level"));

        if (allowRecursive) {
            String resolvedTemplate = resolveTemplate(currentSentence.getStructure().toString(), 0, maxRecursionLevel);
            currentSentence.setStructure(new StringBuilder(resolvedTemplate));
        } else if (currentSentence.getStructure().toString().contains(SENTENCE_TAG)) {
            String adjustedStructure = currentSentence.getStructure().toString().replaceAll(Pattern.quote(SENTENCE_TAG), NOUN_TAG);
            currentSentence.setStructure(new StringBuilder(adjustedStructure));
        }
    }

    // Sostituisce i segnaposti [noun], [verb], [adjective]
    private void replacePlaceholders() {
        String result = currentSentence.getStructure().toString();
        Iterator<String> nounsIterator = currentSentence.getNouns().iterator();
        Iterator<String> verbsIterator = currentSentence.getVerbs().iterator();
        Iterator<String> adjectiveIterator = currentSentence.getAdjectives().iterator();

        while (result.contains(NOUN_TAG) && nounsIterator.hasNext()) {
            result = result.replaceFirst(Pattern.quote(NOUN_TAG), nounsIterator.next());
        }
        while (result.contains(VERB_TAG) && verbsIterator.hasNext()) {
            result = result.replaceFirst(Pattern.quote(VERB_TAG), verbsIterator.next());
        }
        while (result.contains(ADJECTIVE_TAG) && adjectiveIterator.hasNext()) {
            result = result.replaceFirst(Pattern.quote(ADJECTIVE_TAG), adjectiveIterator.next());
        }

        logger.info("Sentence after placeholders replacement: {}", result);
        currentSentence.setSentence(new StringBuilder(result));
    }

    // Capitalizza la prima lettera della frase generata
    private void capitalizeFirstLetter() {
        StringBuilder sentence = currentSentence.getSentence();
        if (!sentence.isEmpty()) {
            sentence.setCharAt(0, Character.toUpperCase(sentence.charAt(0)));
        }
    }

    private String resolveTemplate(String template, int depth, int maxRecursionDepth) {
        if (depth > maxRecursionDepth) {
            return NOUN_TAG;
        }
        while (template.contains(SENTENCE_TAG)) {
            template = template.replaceFirst(Pattern.quote(SENTENCE_TAG), resolveTemplate(template, depth + 1, maxRecursionDepth));
        }
        return template;
    }


    public void setStructureSentenceStrategy(StrategyType strategy, Sentence inputSentence, String selStructure) {
        logger.info("Setting Structure Sentence Strategy: {}", strategy.getClass().getSimpleName());
        switch (strategy) {
            case RANDOM:
                this.structureSentenceStrategy = new RandomStructureStrategy();
                break;
            case SAME:
                this.structureSentenceStrategy = new SameAsAnalyzedStructureStrategy(inputSentence);
                break;
            case SELECTED:
                this.structureSentenceStrategy = new SelectedStructureStrategy(selStructure);
                break;
            default:
                logger.error("Unknown strategy: {}", strategy);
                throw new IllegalArgumentException("Invalid strategy: " + strategy);
        }
    }

    public void validateInput(Sentence inputSentence, boolean newWords, StrategyType strategy) {
        if (inputSentence == null) {
            if (!newWords || strategy.equals(StrategyType.SAME)) {
                throw new IllegalArgumentException("Input sentence cannot be null. Please analyze a sentence first or enable the 'new words' option while selecting a valid structure (Random or Selected).");
            }
        }
    }

    public void configureWordStrategy(boolean newWords, Sentence inputSentence) {
        this.wordSelectionStrategy = newWords ? new NewWordStrategy() : new OriginalWordStrategy(inputSentence);
    }
}
