package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import unipd.edids.logicBusiness.entities.Sentence;

public interface WordSelectionStrategy {
    void populateWords(Sentence temp);
}