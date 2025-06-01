package unipd.edids.logicBusiness.strategies.structureStrategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.entities.Sentence;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SameAsAnalyzedStructureStrategyTest {

    private static final Logger logger = LogManager.getLogger(SameAsAnalyzedStructureStrategyTest.class);
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: SameAsAnalyzedStructureStrategyTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: SameAsAnalyzedStructureStrategyTest");
    }

    @Test
    void testGenerateSentenceStructureValidInputSentenceReturnsCorrectStructure() {
        logger.info("Testing generateSentenceStructure() with a valid input sentence...");
        // Arrange
        Sentence inputSentence = new Sentence("Sample sentence");
        inputSentence.setStructure(new StringBuilder("SVO")); // Example structure
        SameAsAnalyzedStructureStrategy strategy = new SameAsAnalyzedStructureStrategy(inputSentence);

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertEquals("SVO", result.toString(),
                "The generated structure should match the input sentence structure.");
    }

    @Test
    void testConstructorWithNullInputSentenceThrowsIllegalArgumentException() {
        logger.info("Testing constructor with null input sentence...");
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> new SameAsAnalyzedStructureStrategy(null),
                "Constructing the strategy with a null sentence should throw an IllegalArgumentException.");

        assertEquals("The input sentence cannot be null.", exception.getMessage(),
                "Exception message should indicate that the input sentence cannot be null.");
    }

    @Test
    void testGenerateSentenceStructureInputSentenceWithEmptyStructureReturnsEmptyStringBuilder() {
        logger.info("Testing generateSentenceStructure() with an input sentence having an empty structure...");
        // Arrange
        Sentence inputSentence = new Sentence("Empty sentence");
        inputSentence.setStructure(new StringBuilder()); // Empty structure
        SameAsAnalyzedStructureStrategy strategy = new SameAsAnalyzedStructureStrategy(inputSentence);

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertEquals("", result.toString(),
                "The generated structure should be an empty string when the input sentence structure is empty.");
    }

    @Test
    void testGenerateSentenceStructureInputSentenceWithComplexStructureReturnsCorrectStructure() {
        logger.info("Testing generateSentenceStructure() with an input sentence having a complex structure...");
        // Arrange
        Sentence inputSentence = new Sentence("Complex sentence");
        inputSentence.setStructure(new StringBuilder("NP -> VP | PP -> NP VP")); // Complex grammar structure
        SameAsAnalyzedStructureStrategy strategy = new SameAsAnalyzedStructureStrategy(inputSentence);

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertEquals("NP -> VP | PP -> NP VP", result.toString(),
                "The generated structure should match the input complex sentence structure.");
    }
}