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
    String conjugate(String verb);
}
