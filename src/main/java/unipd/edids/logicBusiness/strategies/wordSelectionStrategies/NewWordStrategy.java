package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.factories.WordFactory;

import java.util.List;

/**
 * <p>
 * Responsibilities:
 * - Implements the WordSelectionStrategy interface to provide a specific mechanism for populating words in a Sentence.
 * - Handles the dynamic generation and assignment of nouns, verbs, and adjectives based on the structure of sentences.
 * </p>
 *
 * <p>
 * Design Pattern:
 * - Strategy: Provides a concrete implementation of the WordSelectionStrategy interface to allow flexible word population behavior.
 * </p>
 */
public class NewWordStrategy implements WordSelectionStrategy {

    /**
     * Logger for monitoring and debugging the behavior and executions
     * within the NewWordStrategy class.
     */
    private static final Logger logger = LogManager.getLogger(NewWordStrategy.class);

    /**
     * Populates the noun, verb, and adjective lists of the given Sentence object
     * based on its structure, filling placeholders as needed.
     *
     * @param sentence The Sentence object whose word lists (nouns, verbs, adjectives) will be populated.
     */
    @Override
    public void populateWords(Sentence sentence) {
        logger.info("Populating Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}",
                sentence.getNouns(), sentence.getVerbs(), sentence.getAdjectives());

        populateWordList(sentence.getNouns(), sentence.getStructure(), "[noun]", WordFactory.WordType.NOUN);
        populateWordList(sentence.getVerbs(), sentence.getStructure(), "[verb]", WordFactory.WordType.VERB);
        populateWordList(sentence.getAdjectives(), sentence.getStructure(), "[adjective]", WordFactory.WordType.ADJECTIVE);

        logger.info("Populating Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}",
                sentence.getNouns(), sentence.getVerbs(), sentence.getAdjectives());
    }

    /**
     * Popola la lista di parole specificata fino a raggiungere il numero richiesto basato sulla struttura.
     *
     * @param wordList    La lista di parole da popolare (e.g., nouns, verbs, adjectives).
     * @param structure   La struttura della frase in cui cercare i segnaposto.
     * @param placeholder Il segnaposto da cercare nella struttura (e.g., "[noun]").
     * @param wordType    Il tipo di parola da generare (e.g., NOUN, VERB, ADJECTIVE).
     */
    private void populateWordList(List<String> wordList, StringBuilder structure, String placeholder, WordFactory.WordType wordType) {
        int requiredCount = StringUtils.countMatches(structure, placeholder);
        while (wordList.size() < requiredCount) {
            wordList.add(WordFactory.getWordProvider(wordType).getRandomWord());
        }
    }
   }