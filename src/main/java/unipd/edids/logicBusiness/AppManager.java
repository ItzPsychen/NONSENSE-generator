package unipd.edids.logicBusiness;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.exceptions.AnalyzeException;
import unipd.edids.logicBusiness.exceptions.GenerateException;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;
import unipd.edids.logicBusiness.services.AnalyzeSentenceService;
import unipd.edids.logicBusiness.services.GenerateSentenceService;
import unipd.edids.logicBusiness.services.ModerationSentenceService;
import unipd.edids.logicBusiness.strategies.structureStrategies.StrategyType;

/**
 * Acts as a Facade for the core application logic, managing sentence analysis, generation, and moderation.
 *
 * <p>Main Responsibilities:
 * - Provides a simplified interface for the underlying services:
 * - Sentence analysis via {@link AnalyzeSentenceService}.
 * - Sentence generation via {@link GenerateSentenceService}.
 * - Sentence moderation via {@link ModerationSentenceService}.
 * - Manages input and output sentences.
 * - Offers utilities for clearing and retrieving processed sentences.
 * - Handles application configurations via {@link ConfigManager}.
 *
 * <p>Design Pattern:
 * - Implements the Facade pattern to centralize and simplify the access to sentence-related operations
 * by delegating tasks to underlying services while minimizing direct dependencies among components.
 * - Utilizes the Singleton pattern for {@link ConfigManager} initialization.
 */
public class AppManager {
    /**
     * Logger instance for the AppManager class.
     * Used for logging messages and tracking application behavior.
     */
    private static final Logger logger = LogManager.getLogger(AppManager.class);

    /**
     * The AnalyzeSentenceService is responsible for performing syntax analysis on text input.
     */
    private final AnalyzeSentenceService analyzeSentenceService;

    /**
     * Responsible for generating sentences based on specified structure and word selection strategies.
     */
    private final GenerateSentenceService generateSentenceService;

    /**
     * Service responsible for moderating the sentences by analyzing their toxicity
     * and adjusting content according to predefined categories.
     */
    private final ModerationSentenceService moderationSentenceService;

    /**
     * Manages application configuration settings.
     */
    private final ConfigManager configManager;

    /**
     * Represents the sentence input provided to the application.
     */
    private Sentence inputSentence;

    /**
     * Stores the output sentence generated or analyzed by the application.
     */
    private Sentence outputSentence;

    /**
     * Constructs an instance of the AppManager Facade.
     * Initializes the core services responsible for sentence analysis, generation, and moderation.
     * Additionally, it applies the singleton instance of {@link ConfigManager} to manage configuration settings.</p>
     */
    public AppManager(ConfigManager configManager) {
        this.analyzeSentenceService = new AnalyzeSentenceService();
        this.generateSentenceService = new GenerateSentenceService();
        this.moderationSentenceService = new ModerationSentenceService();
        this.configManager = configManager;
    }

    /**
     * Analyzes the syntax of a sentence and optionally saves the analyzed sentence to a file.
     *
     * @param text         the input text to analyze.
     * @param saveSelected a boolean indicating whether the analyzed sentence should be saved to a file.
     * @return an instance of {@code Sentence} containing the details of the analyzed sentence.
     * @throws AnalyzeException if an error occurs during the analysis process.
     */
    public Sentence analyzeSentence(String text, boolean saveSelected) {
        logger.info("Starting sentence analysis. Input text: '{}', saveSelected: {}", text, saveSelected);
        try {
            inputSentence = analyzeSentenceService.analyzeSyntax(text);
            logger.debug("Sentence successfully analyzed: {}", inputSentence);

            if (saveSelected) {
                String savePath = configManager.getProperty("analyzed.save.file");
                FileManager.appendLineToSavingFile(savePath, inputSentence.toString());
                logger.info("Analyzed sentence saved to file: {}", savePath);
            }

            logger.info("Sentence analysis completed successfully.");
            return inputSentence;
        } catch (Exception e) {
            String errorMessage = "Sentence analysis failed: " + e.getClass().getSimpleName() + " - " + e.getMessage();
            logger.error(errorMessage, e);
            throw new AnalyzeException(errorMessage, e);
        }
    }

    /**
     * Generates a sentence based on the specified strategy, input structure, and configuration options.
     *
     * @param strategyName     The strategy for sentence structure determination (e.g., RANDOM, SAME, SELECTED).
     * @param selStructure The selected structure to use when the SELECTED strategy is applied.
     * @param toxicity     Whether to moderate the generated sentence for toxicity.
     * @param futureTense  Specifies whether to configure the sentence with a future verb tense.
     * @param newWords     Configures if new words should be used in the generated sentence.
     * @param saveSelected Whether to save the generated sentence to a persistent storage.
     * @return A {@code Sentence} object containing the generated sentence, structure, and related metadata.
     * @throws GenerateException If the sentence generation process encounters an error.
     */
    public Sentence generateSentence(String strategyName, String selStructure, boolean toxicity, boolean futureTense, boolean newWords, boolean saveSelected) {
        logger.info("Starting sentence generation with params [strategy: {}, selStructure: {}, toxicity: {}, futureTense: {}, newWords: {}, saveSelected: {}]", strategyName, selStructure, toxicity, futureTense, newWords, saveSelected);
        try {
            StrategyType strategy = StrategyType.valueOf(strategyName.toUpperCase());

            // Validazione dell'input
            logger.debug("Validating input sentence: {}", inputSentence);
            generateSentenceService.validateInput(inputSentence, newWords, strategy);

            // Imposta le strategie e genera la frase
            logger.debug("Setting structure strategy: {}", strategy);
            generateSentenceService.setStructureSentenceStrategy(strategy, inputSentence, selStructure);

            logger.debug("Configuring verb tense: futureTense = {}", futureTense);
            Verb.getInstance().configureVerbTense(futureTense);

            logger.debug("Configuring word strategy: newWords = {}", newWords);
            generateSentenceService.configureWordStrategy(newWords, inputSentence);

            outputSentence = generateSentenceService.generateSentence();
            logger.debug("Generated sentence: {}", outputSentence);

            // Moderazione della tossicità
            if (toxicity) {
                logger.debug("Moderating sentence for toxicity...");
                moderationSentenceService.moderateText(outputSentence);
                logger.info("Toxicity moderation completed.");
            }

            // Salvataggio della frase
            if (saveSelected) {
                String savePath = configManager.getProperty("generated.save.file");
                FileManager.appendLineToSavingFile(savePath, outputSentence.toString());
                logger.info("Generated sentence saved to file: {}", savePath);
            }

            logger.info("Sentence generation completed successfully.");
            return outputSentence;
        } catch (Exception e) {
            String errorMessage = "Sentence generation failed: " + e.getClass().getSimpleName() + " - " + e.getMessage();
            logger.error(errorMessage, e);
            throw new GenerateException(errorMessage, e);
        }
    }


    /**
     * Resets the input and output sentences to a null state.
     */
    public void clearAll() {
        logger.info("Clearing input and output sentences.");
        this.inputSentence = null;
        this.outputSentence = null;
    }

    /**
     * Provides the current output sentence generated or processed by the application.
     *
     * @return the current output Sentence instance, or null if no sentence has been processed or generated.
     */
    public Sentence getOutputSentence() {
        return outputSentence;
    }
}
