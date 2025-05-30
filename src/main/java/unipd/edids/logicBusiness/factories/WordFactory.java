package unipd.edids.logicBusiness.factories;

import unipd.edids.logicBusiness.entities.Adjective;
import unipd.edids.logicBusiness.entities.Noun;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.entities.Word;

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