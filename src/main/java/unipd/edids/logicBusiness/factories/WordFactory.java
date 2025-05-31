package unipd.edids.logicBusiness.factories;

import unipd.edids.logicBusiness.entities.Adjective;
import unipd.edids.logicBusiness.entities.Noun;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.entities.Word;

/**
 * The WordFactory class is a factory responsible for providing instances of specific word type providers such as Noun, Adjective, or Verb.
 *
 * <p>Responsibilities:
 * - Defines and manages supported word types via the WordType enum.
 * - Provides factory methods to return appropriate singleton instances of word type providers based on the requested WordType.
 *
 * <p>Design Pattern:
 * - Factory Method Pattern: Creates instances of specific word providers (Noun, Adjective, Verb) based on input parameters.
 */
public class WordFactory {

    /**
     * Represents the types of words supported within the system.
     */
    // Enum per definire i tipi di parole supportati
    public enum WordType {
        NOUN,
        ADJECTIVE,
        VERB
    }

    /**
     * Factory method to return the corresponding word provider based on the given WordType.
     *
     * @param wordType The type of word provider to be returned (e.g., NOUN, ADJECTIVE, VERB).
     * @return The singleton instance of the corresponding Word provider.
     */
    // Metodo factory per restituire il provider corrispondente
    public static Word getWordProvider(WordType wordType) {
        return switch (wordType) {
            case NOUN -> Noun.getInstance();
            case ADJECTIVE -> Adjective.getInstance();
            case VERB -> Verb.getInstance();
        };
    }
}