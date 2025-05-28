package unipd.edids.entities;

import unipd.edids.SentenceStructure;

public class WordFactory {

    // Enum per definire i tipi di parole supportati
    public enum WordType {
        NOUN,
        ADJECTIVE,
        VERB
    }

    // Metodo factory per restituire il provider corrispondente
    public static Word getWordProvider(WordType wordType) {
        return switch (wordType) {
            case NOUN -> Noun.getInstance();
            case ADJECTIVE -> Adjective.getInstance();
            case VERB -> Verb.getInstance();
        };
    }
}