package unipd.edids.logicBusiness.strategies.tenseStrategies;

/**
 * The TenseStrategy interface provides a mechanism for implementing different strategies
 * to conjugate verbs into various tenses.
 *
 * <p>Responsibilities:
 * - Defines a common interface for conjugating verbs.
 * - Facilitates dynamic switching between different verb conjugation strategies.
 *
 * <p>Design Pattern:
 * - Strategy: Encapsulates distinct verb conjugation logic into interchangeable implementations of this interface.
 */
// Interfaccia Strategy
public interface TenseStrategy {
    /**
     * Conjugates the provided verb into the appropriate tense.
     *
     * @param verb the verb to conjugate; must not be null.
     * @return the conjugated form of the provided verb.
     * @throws IllegalArgumentException if the verb parameter is null.
     */
    String conjugate(String verb);
}
