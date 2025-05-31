package unipd.edids.logicBusiness.strategies.tenseStrategies;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FutureTenseStrategyTest {

    @Test
    void testConjugateWithNullInput() {
        FutureTenseStrategy strategy = new FutureTenseStrategy();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> strategy.conjugate(null),
                "Expected IllegalArgumentException when providing a null verb");
        assertEquals("The verb cannot be null.", exception.getMessage(),
                "Expected exception message to be 'The verb cannot be null.'");
    }

    @Test
    void testConjugateWithSpecialCaseVerbIs() {
        FutureTenseStrategy strategy = new FutureTenseStrategy();
        String result = strategy.conjugate("is");
        assertEquals("will be", result,
                "Conjugation for 'is' is incorrect, expected 'will be'.");
    }

    @Test
    void testConjugateWithSpecialCaseVerbAm() {
        FutureTenseStrategy strategy = new FutureTenseStrategy();
        String result = strategy.conjugate("am");
        assertEquals("will be", result,
                "Conjugation for 'am' is incorrect, expected 'will be'.");
    }

    @Test
    void testConjugateWithSpecialCaseVerbAre() {
        FutureTenseStrategy strategy = new FutureTenseStrategy();
        String result = strategy.conjugate("are");
        assertEquals("will be", result,
                "Conjugation for 'are' is incorrect, expected 'will be'.");
    }

    @Test
    void testConjugateWithRegularVerb() {
        FutureTenseStrategy strategy = new FutureTenseStrategy();
        String result = strategy.conjugate("run");
        assertEquals("will run", result,
                "Conjugation for 'run' is incorrect, expected 'will run'.");
    }

    @Test
    void testConjugateWithEmptyString() {
        FutureTenseStrategy strategy = new FutureTenseStrategy();
        String result = strategy.conjugate("");
        assertEquals("will ", result,
                "Conjugation for an empty string is incorrect, expected 'will '.");
    }

    @Test
    void testConjugateWithCapitalizedVerb() {
        FutureTenseStrategy strategy = new FutureTenseStrategy();
        String result = strategy.conjugate("Walk");
        assertEquals("will Walk", result,
                "Conjugation for 'Walk' is incorrect, expected 'will Walk'.");
    }
}