package unipd.edids.logicBusiness.strategies.structureStrategies;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SelectedStructureStrategyTest {

    @BeforeAll
    static void setupClass() {
        // Any global setup that might be required for the tests
        System.out.println("Starting SelectedStructureStrategyTest...");
    }

    @AfterAll
    static void tearDownClass() {
        // Any global teardown after all tests execution
        System.out.println("Finished SelectedStructureStrategyTest.");
    }

    /**
     * Test class for SelectedStructureStrategy.
     * <p>
     * The SelectedStructureStrategy class implements the StructureSentenceStrategy interface.
     * It takes a string representing the selected sentence structure and provides a method to
     * generate it as a StringBuilder.
     * <p>
     * Methods being tested:
     * - generateSentenceStructure(): Returns a StringBuilder initialized with the selected structure.
     * - SelectedStructureStrategy(): Ensures proper initialization and input validation.
     */

    @Test
    void testGenerateSentenceStructureWithSpecialCharacters() {
        // Given
        String inputStructure = "@!#$%^&*()_+~`|}{[]:;?><,./-=\\";
        SelectedStructureStrategy strategy = new SelectedStructureStrategy(inputStructure);

        // When
        StringBuilder result = strategy.generateSentenceStructure();

        // Then
        assertNotNull(result, "The result should not be null.");
        assertEquals(inputStructure, result.toString(),
                "The generated sentence structure should match the input structure containing special characters.");
    }

    @Test
    void testGenerateSentenceStructureWithVeryLongString() {
        // Given
        String inputStructure = "a".repeat(10000); // 10,000 characters string
        SelectedStructureStrategy strategy = new SelectedStructureStrategy(inputStructure);

        // When
        StringBuilder result = strategy.generateSentenceStructure();

        // Then
        assertNotNull(result, "The result should not be null.");
        assertEquals(inputStructure, result.toString(),
                "The generated sentence structure should handle and match the very long input structure.");
    }

    @Test
    void testGenerateSentenceStructureWithEmptyString() {
        // Given
        String inputStructure = "";
        SelectedStructureStrategy strategy = new SelectedStructureStrategy(inputStructure);

        // When
        StringBuilder result = strategy.generateSentenceStructure();

        // Then
        assertNotNull(result, "The result should not be null.");
        assertEquals(inputStructure, result.toString(),
                "The generated sentence structure should match the input structure (which is an empty string).");
    }

    @Test
    void testConstructorThrowsExceptionForNullInput() {
        // Given
        String inputStructure = null;



        // When & Then
        IllegalArgumentException exception =
            org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                    () -> new SelectedStructureStrategy(inputStructure),
                    "Constructor should throw IllegalArgumentException when input is null.");

        assertEquals("The selected structure cannot be null.", exception.getMessage(),
                "Exception message should indicate that null input is not allowed.");
    }
}