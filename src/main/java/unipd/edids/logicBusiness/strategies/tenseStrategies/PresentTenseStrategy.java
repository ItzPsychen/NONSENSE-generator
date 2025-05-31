package unipd.edids.logicBusiness.strategies.tenseStrategies;

/**
 * The PresentTenseStrategy class is a concrete implementation of the TenseStrategy interface
 * designed for conjugating verbs into their present tense form.
 *
 * <p>Responsibilities:
 * - Implements the logic for conjugating verbs into the present tense.
 * - Acts as a component within a strategy pattern, interchangeable for different
 *   tense-conjugation strategies.
 *
 * <p>Design Pattern:
 * - Strategy: Implements one specific behavior (present tense conjugation) among
 *   interchangeable strategies for verb conjugation.
 */
public class PresentTenseStrategy implements TenseStrategy {
    /**
     * Conjugates the given verb into the present tense form.
     *
     * @param verb the verb to conjugate; must not be null.
     * @return the conjugated verb in its present tense form.
     * @throws IllegalArgumentException if the verb parameter is null.
     */
    @Override
    public String conjugate(String verb) {
        if(verb == null)
            throw new IllegalArgumentException("The verb cannot be null.");
        return verb;
    }
}
