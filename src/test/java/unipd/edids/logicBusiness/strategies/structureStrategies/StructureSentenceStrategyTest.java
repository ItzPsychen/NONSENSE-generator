package unipd.edids.logicBusiness.strategies.structureStrategies;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StructureSentenceStrategyTest {

    /**
     * This test class tests the functionality of the `generateSentenceStructure` method
     * in classes implementing the `StructureSentenceStrategy` interface. The purpose of the
     * method is to generate and return a sentence structure as a StringBuilder object.
     * <p>
     * Each test case will cover individual scenarios to ensure the method behaves as expected.
     */

    @Test
    void testGenerateSentenceStructureForDefaultImplementation() {
        // Arrange: Using an anonymous implementation of StructureSentenceStrategy
        StructureSentenceStrategy strategy = new StructureSentenceStrategy() {
            @Override
            public StringBuilder generateSentenceStructure() {
                return new StringBuilder("Default Sentence Structure");
            }
        };

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "Resulting StringBuilder should not be null");
        assertEquals("Default Sentence Structure", result.toString(),
                "The returned sentence structure does not match the expected value");
    }

    @Test
    void testGenerateSentenceStructureForEmptyStructure() {
        // Arrange: Using an anonymous implementation with an empty structure
        StructureSentenceStrategy emptyStrategy = new StructureSentenceStrategy() {
            @Override
            public StringBuilder generateSentenceStructure() {
                return new StringBuilder();
            }
        };

        // Act
        StringBuilder result = emptyStrategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "Resulting StringBuilder should not be null even for an empty structure");
        assertEquals("", result.toString(), "Expected an empty sentence structure but got a different result");
    }

    @Test
    void testGenerateSentenceStructureForCustomStructure() {
        // Arrange: Using an anonymous implementation with a custom structure
        StructureSentenceStrategy customStrategy = new StructureSentenceStrategy() {
            @Override
            public StringBuilder generateSentenceStructure() {
                return new StringBuilder("This is a custom structure.");
            }
        };

        // Act
        StringBuilder result = customStrategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "Resulting StringBuilder should not be null");
        assertEquals("This is a custom structure.", result.toString(),
                "The returned sentence structure does not match the expected custom value");
    }
}