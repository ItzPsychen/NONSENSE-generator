package unipd.edids.logicBusiness.strategies.tenseStrategies;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PresentTenseStrategyTest {

    /**
     * Test class for PresentTenseStrategy.
     * This class ensures the `conjugate` method in PresentTenseStrategy class
     * behaves as expected under various scenarios.
     */

    @Test
    void testConjugateWithValidVerb() {
        // Arrange
        PresentTenseStrategy strategy = new PresentTenseStrategy();
        String inputVerb = "run";

        // Act
        String result = strategy.conjugate(inputVerb);

        // Assert
        assertEquals("run", result, "The method should return the input verb without any changes.");
    }

    @Test
    void testConjugateWithEmptyString() {
        // Arrange
        PresentTenseStrategy strategy = new PresentTenseStrategy();
        String inputVerb = "";

        // Act
        String result = strategy.conjugate(inputVerb);

        // Assert
        assertEquals("", result, "The method should return the input string even if it is empty.");
    }

    @Test
    void testConjugateWithNullVerb() {
        // Arrange
        PresentTenseStrategy strategy = new PresentTenseStrategy();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> strategy.conjugate(null),
                "The method should throw an IllegalArgumentException when the input is null."
        );
        assertEquals("The verb cannot be null.", exception.getMessage(),
                "The exception message should indicate that the verb cannot be null.");
    }
}