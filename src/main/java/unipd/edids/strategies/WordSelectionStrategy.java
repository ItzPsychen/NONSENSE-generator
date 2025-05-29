package unipd.edids.strategies;

import unipd.edids.Sentence;

public interface WordSelectionStrategy {
    void populateWords(Sentence temp);
}