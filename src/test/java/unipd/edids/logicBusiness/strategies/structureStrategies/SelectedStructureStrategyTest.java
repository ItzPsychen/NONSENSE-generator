package unipd.edids.logicBusiness.strategies.structureStrategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SelectedStructureStrategyTest {

    private static final Logger logger = LogManager.getLogger(SelectedStructureStrategyTest.class);
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: SelectedStructureStrategyTest");
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
        logger.info("Finished test suite: SelectedStructureStrategyTest");
    }

    @Test
    void testGenerateSentenceStructureWithSpecialCharacters() {
        logger.info("Testing generateSentenceStructure() with special characters...");
        // Arrange
        String inputStructure = "@!#$%^&*()_+~`|}{[]:;?><,./-=\\";
        SelectedStructureStrategy strategy = new SelectedStructureStrategy(inputStructure);

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(inputStructure, result.toString(),
                "The generated sentence structure should match the input structure containing special characters.");
    }

    @Test
    void testGenerateSentenceStructureWithVeryLongString() {
        logger.info("Testing generateSentenceStructure() with a very long string...");
        // Arrange
        String inputStructure = "a".repeat(10000); // 10,000 characters string
        SelectedStructureStrategy strategy = new SelectedStructureStrategy(inputStructure);

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(inputStructure, result.toString(),
                "The generated sentence structure should handle and match the very long input structure.");
    }

    @Test
    void testGenerateSentenceStructureWithEmptyString() {
        logger.info("Testing generateSentenceStructure() with an empty string...");
        // Arrange
        String inputStructure = "";
        SelectedStructureStrategy strategy = new SelectedStructureStrategy(inputStructure);

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(inputStructure, result.toString(),
                "The generated sentence structure should match the input structure (which is an empty string).");
    }

    @Test
    void testConstructorThrowsExceptionForNullInput() {
        logger.info("Testing constructor with null input...");
        // Arrange
        String inputStructure = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new SelectedStructureStrategy(inputStructure),
                "Constructor should throw IllegalArgumentException when input is null.");

        assertEquals("The selected structure cannot be null.", exception.getMessage(),
                "Exception message should indicate that null input is not allowed.");
    }
}