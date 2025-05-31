package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import unipd.edids.logicBusiness.entities.Sentence;

/**
 * <p>
 * Responsibilities:
 * - Defines a strategy for selecting and populating words into a Sentence object.
 * - Ensures flexibility and extensibility in word processing by leveraging the Strategy design pattern.
 * </p>
 */
public interface WordSelectionStrategy {
    void populateWords(Sentence temp);
}