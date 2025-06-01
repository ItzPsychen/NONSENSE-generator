package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.factories.WordFactory;
import unipd.edids.logicBusiness.strategies.tenseStrategies.FutureTenseStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * <p>
 * Responsibilities:
 * - Implements a word selection strategy to retain and process the original words of an input sentence.
 * - Utilizes the Strategy design pattern to allow flexible switching between various word processing strategies.
 * </p>
 */
public class OriginalWordStrategy implements WordSelectionStrategy {
    /**
     * Represents the logger for the `OriginalWordStrategy` class.
     * Used for logging important runtime information or debugging messages.
     */
    private static final Logger logger = LogManager.getLogger(OriginalWordStrategy.class);
    /**
     * Represents the source sentence used as input for processing strategies.
     * Stores original word lists and sentence structure.
     */
    final Sentence inputSentence;

    /**
     * <p>
     * Responsibilities:
     * - Implements a word selection strategy to retain and process the original words of an input sentence.
     * - Utilizes the Strategy design pattern to provide flexibility in word processing approaches.
     * </p>
     *
     * @param inputSentence the {@link Sentence} object that serves as the source of original words
     *                      for this strategy.
     */
    public OriginalWordStrategy(Sentence inputSentence) {
        this.inputSentence = inputSentence;
    }

    /**
     * Populates word lists (nouns, verbs, adjectives) in the provided temporary sentence
     * and modifies its structure accordingly. Handles tasks such as copying words
     * from an input sentence, generating additional words, verb conjugation, and shuffling.
     *
     * @param tempSentence the temporary {@link Sentence} object to populate with words.
     *                      This object gets updated with nouns, verbs, and adjectives,
     *                      as well as a revised sentence structure.
     */
    @Override
    public void populateWords(Sentence tempSentence) {
        logger.debug("Starting to populate words for the sentence structure: {}", tempSentence.getStructure());

        try {
            copyInputSentenceWords(tempSentence);
            logger.debug("Successfully copied input sentence words into tempSentence.");

            populateWordList(tempSentence.getStructure(), "[noun]", tempSentence.getNouns(), WordFactory.WordType.NOUN);
            logger.debug("Populated nouns: {}", tempSentence.getNouns());

            handleFutureTenseConjugation(tempSentence);
            logger.debug("Handled future tense conjugation for verbs: {}", tempSentence.getVerbs());

            populateWordList(tempSentence.getStructure(), "[verb]", tempSentence.getVerbs(), WordFactory.WordType.VERB);
            logger.debug("Populated verbs: {}", tempSentence.getVerbs());

            populateWordList(tempSentence.getStructure(), "[adjective]", tempSentence.getAdjectives(), WordFactory.WordType.ADJECTIVE);
            logger.debug("Populated adjectives: {}", tempSentence.getAdjectives());

            logWordLists(tempSentence);
            shuffleWordLists(tempSentence);
            logger.debug("Shuffled word lists successfully.");
        } catch (Exception e) {
            String errorMessage = "An error occurred while populating words: " + e.getMessage();
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }

        logger.debug("Finished populating words for the sentence.");
    }

    /**
     * Copies the nouns, verbs, and adjectives from the input sentence to the provided temporary sentence.
     *
     * @param temp the temporary {@link Sentence} object where words (nouns, verbs, adjectives) will be copied.
     */
    private void copyInputSentenceWords(Sentence temp) {
        logger.debug("Copying words from input sentence (nouns, verbs, adjectives).");
        temp.setNouns(new ArrayList<>(inputSentence.getNouns()));
        temp.setVerbs(new ArrayList<>(inputSentence.getVerbs()));
        temp.setAdjectives(new ArrayList<>(inputSentence.getAdjectives()));
    }

    /**
     * Fills the provided word list with randomly generated words of the specified type based on the placeholders
     * in the sentence structure.
     *
     * @param structure   the sentence structure containing placeholder tokens (e.g., "[noun]", "[verb]")
     *                    indicating where words need to be added.
     * @param placeholder the specific placeholder token to match in the structure (e.g., "[noun]").
     * @param wordList    the list to be populated with generated words of the specified type.
     * @param wordType    the type of word to generate (e.g., NOUN, VERB, ADJECTIVE).
     */
    private void populateWordList(StringBuilder structure, String placeholder, List<String> wordList, WordFactory.WordType wordType) {
        logger.debug("Populating word list for type: {} with placeholder: {}", wordType, placeholder);
        while (StringUtils.countMatches(structure, placeholder) - wordList.size() > 0) {
            wordList.add(WordFactory.getWordProvider(wordType).getRandomWord());
        }
        logger.debug("Finished populating word list for type: {}. Current list: {}", wordType, wordList);
    }

    /**
     * Handles the process of conjugating verbs in a sentence to the future tense
     * based on the current tense strategy.
     *
     * @param temp the {@link Sentence} object containing the list of verbs to conjugate.
     *             The list is updated with their future tense forms if the active
     *             tense strategy is of type {@code FutureTenseStrategy}.
     */
    private void handleFutureTenseConjugation(Sentence temp) {
        logger.debug("Checking if FutureTenseStrategy is applied for verbs.");
        if (Verb.getInstance().getTenseStrategy() instanceof FutureTenseStrategy) {
            logger.debug("FutureTenseStrategy detected. Starting to conjugate verbs.");
            List<String> conjugatedVerbs = new ArrayList<>();
            for (String verb : temp.getVerbs()) {
                conjugatedVerbs.add(Verb.getInstance().conjugate(verb));
            }
            temp.setVerbs(conjugatedVerbs);
            logger.debug("Completed conjugation. Conjugated verbs: {}", conjugatedVerbs);
        }
    }

    /**
     * Logs the populated word lists (nouns, verbs, adjectives) for the provided temporary {@link Sentence} object.
     *
     * @param temp the {@link Sentence} object containing the lists of nouns, verbs, and adjectives to be logged.
     */
    private void logWordLists(Sentence temp) {
        logger.info("Populated Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}",
                temp.getNouns(), temp.getVerbs(), temp.getAdjectives());
    }

    /**
     * Shuffles the word lists (nouns, verbs, and adjectives) of the given {@link Sentence} object.
     *
     * @param temp the {@link Sentence} object whose word lists (nouns, verbs, adjectives) will be shuffled.
     */
    private void shuffleWordLists(Sentence temp) {
        logger.debug("Shuffling word lists for nouns, verbs, and adjectives.");
        Collections.shuffle(temp.getNouns());
        Collections.shuffle(temp.getVerbs());
        Collections.shuffle(temp.getAdjectives());
        logger.debug("Shuffling completed for all word lists.");
    }
}