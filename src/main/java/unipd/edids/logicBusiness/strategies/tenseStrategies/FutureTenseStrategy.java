package unipd.edids.logicBusiness.strategies.tenseStrategies;

/**
 * The FutureTenseStrategy class is a concrete implementation of the TenseStrategy interface
 * designed for conjugating verbs into their future tense form.
 *
 * <p>Responsibilities:
 * - Implements the logic for conjugating verbs into future tense.
 * - Acts as a strategy component, interchangeable with other tense-conjugation strategies.
 *
 * <p>Design Pattern:
 * - Strategy: Implements specific behavior (future tense conjugation) among interchangeable verb conjugation strategies.
 */
public class FutureTenseStrategy implements TenseStrategy {
    /**
     * Conjugates the given verb into its future tense form.
     *
     * @param verb the verb to conjugate; must not be null.
     * @return the conjugated verb in its future tense form.
     * @throws IllegalArgumentException if the verb parameter is null.
     */
    @Override
    public String conjugate(String verb) {
        if(verb == null)
            throw new IllegalArgumentException("The verb cannot be null.");
        if(verb.equals("is") || verb.equals("am") || verb.equals("are"))
            return "will be";
        return "will " + verb;
    }
}
