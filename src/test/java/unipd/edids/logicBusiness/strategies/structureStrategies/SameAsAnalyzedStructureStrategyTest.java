package unipd.edids.logicBusiness.strategies.structureStrategies;

import org.junit.jupiter.api.Test;
import unipd.edids.logicBusiness.entities.Sentence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SameAsAnalyzedStructureStrategyTest {

    /**
     * Tests for the generateSentenceStructure method in SameAsAnalyzedStructureStrategy class.
     * <p>
     * Class being tested:
     * SameAsAnalyzedStructureStrategy: This class is a strategy that generates a sentence structure
     * identical to the analyzed input sentence's structure, passed during object instantiation.
     * <p>
     * Method being tested:
     * generateSentenceStructure: Returns a new StringBuilder containing the structure of the input sentence.
     */

    @Test
    void testGenerateSentenceStructure_ValidInputSentence_ReturnsCorrectStructure() {
        // Arrange
        Sentence inputSentence = new Sentence("Sample sentence");
        inputSentence.setStructure(new StringBuilder("SVO")); // Example structure
        SameAsAnalyzedStructureStrategy strategy = new SameAsAnalyzedStructureStrategy(inputSentence);

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertEquals("SVO", result.toString(), "The generated structure should match the input sentence structure.");
    }

    @Test
    void testConstructor_NullInputSentence_ThrowsIllegalArgumentException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> new SameAsAnalyzedStructureStrategy(null),
                "Constructing the strategy with a null sentence should throw an IllegalArgumentException.");
        assertEquals("The input sentence cannot be null.", exception.getMessage(),
                "Exception message should indicate that the input sentence cannot be null.");
    }

    @Test
    void testGenerateSentenceStructure_InputSentenceWithEmptyStructure_ReturnsEmptyStringBuilder() {
        // Arrange
        Sentence inputSentence = new Sentence("Empty sentence");
        inputSentence.setStructure(new StringBuilder("")); // Empty structure
        SameAsAnalyzedStructureStrategy strategy = new SameAsAnalyzedStructureStrategy(inputSentence);

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertEquals("", result.toString(), "The generated structure should be an empty string when the input sentence structure is empty.");
    }

    @Test
    void testGenerateSentenceStructure_InputSentenceWithComplexStructure_ReturnsCorrectStructure() {
        // Arrange
        Sentence inputSentence = new Sentence("Complex sentence");
        inputSentence.setStructure(new StringBuilder("NP -> VP | PP -> NP VP")); // Complex grammar structure
        SameAsAnalyzedStructureStrategy strategy = new SameAsAnalyzedStructureStrategy(inputSentence);

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertEquals("NP -> VP | PP -> NP VP", result.toString(), "The generated structure should match the input complex sentence structure.");
    }

}