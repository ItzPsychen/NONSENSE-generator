package unipd.edids.logicBusiness.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.exceptions.MissingApiKeyException;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnalyzeSentenceServiceTest {

    private static final Logger logger = LogManager.getLogger(AnalyzeSentenceServiceTest.class);
    private int testNumber = 0;
    private AnalyzeSentenceService analyzeSentenceService;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: AnalyzeSentenceServiceTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
        analyzeSentenceService = new AnalyzeSentenceService();
    }

    @AfterEach
    void tearDown() throws IOException {
        logger.info("Finished test #{}", testNumber);
        analyzeSentenceService = null;

        // Reset configurations and clean up files
        ConfigManager.getInstance().resetDefault();
        File file = new File("testFile.txt");
        if (file.exists() && !file.delete()) {
            logger.warn("Failed to delete test file: testFile.txt");
        }
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: AnalyzeSentenceServiceTest");
    }

    @Test
    void testAnalyzeSyntax_validInput() {
        logger.info("Testing analyzeSyntax() with valid input...");
        // Arrange
        String text = "The quick brown fox jumps over the lazy dog";

        try {
            // Gets the API key; throws MissingApiKeyException if not configured.
            ConfigManager.getInstance().getProperty("api.key.file");
        } catch (MissingApiKeyException e) {
            logger.warn("Skipping test due to missing API Key: {}", e.getMessage());
            Assumptions.assumeTrue(false, "Test skipped: API Key is not configured.");
        }

        // Act
        Sentence result = analyzeSentenceService.analyzeSyntax(text);



        // Assert (eseguiti solo se non viene lanciata MissingApiKeyException)
        assertNotNull(result, "Resulting sentence should not be null");
        assertEquals(text, result.getSentence().toString(), "Sentence content should match the input text");
        assertFalse(result.getNouns().isEmpty(), "Nouns list should not be empty");
        assertFalse(result.getVerbs().isEmpty(), "Verbs list should not be empty");
        assertFalse(result.getAdjectives().isEmpty(), "Adjectives list should not be empty");
        assertNotNull(result.getSyntaxTree(), "Syntax tree should not be null");

    }

    @Test
    void testAnalyzeSyntax_emptyInput() {
        logger.info("Testing analyzeSyntax() with empty input...");
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> analyzeSentenceService.analyzeSyntax(""),
                "Empty text should throw IllegalArgumentException");
        assertEquals("Input text cannot be null or empty.", exception.getMessage(),
                "Error message should match the expected text");
    }

    @Test
    void testAnalyzeSyntax_nullInput() {
        logger.info("Testing analyzeSyntax() with null input...");
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> analyzeSentenceService.analyzeSyntax(null),
                "Null text should throw IllegalArgumentException");
        assertEquals("Input text cannot be null or empty.", exception.getMessage(),
                "Error message should match the expected text");
    }

    @Test
    void testAnalyzeSyntax_exceedsMaxLength() throws IOException {
        logger.info("Testing analyzeSyntax() for input exceeding max length...");
        // Arrange
        ConfigManager.getInstance().setProperty("max.sentence.length", "10");
        String text = "This text exceeds the maximum allowed length";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> analyzeSentenceService.analyzeSyntax(text),
                "Text exceeding max length should throw IllegalArgumentException");
        assertEquals("Input text cannot exceed 10 characters.", exception.getMessage(),
                "Error message should reflect the exceeded maximum length");
    }

    @Test
    void testAnalyzeSyntax_invalidCharactersStart() {
        logger.info("Testing analyzeSyntax() for input with invalid start characters...");
        // Arrange
        String text = "@Invalid start";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> analyzeSentenceService.analyzeSyntax(text),
                "Text with invalid start character should throw IllegalArgumentException");
        assertEquals("Input text contains invalid characters at the start of the text.",
                exception.getMessage(),
                "Error message should match expected for invalid start characters");
    }

    @Test
    void testAnalyzeSyntax_invalidCharactersEnd() {
        logger.info("Testing analyzeSyntax() for input with invalid end characters...");
        // Arrange
        String text = "Valid text%";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> analyzeSentenceService.analyzeSyntax(text),
                "Text with invalid end character should throw IllegalArgumentException");
        assertEquals("Input text contains invalid characters at the end of the text.",
                exception.getMessage(),
                "Error message should match expected for invalid end characters");
    }

    @Test
    void testAnalyzeSyntax_invalidCharactersMid() {
        logger.info("Testing analyzeSyntax() for input with invalid characters in the middle...");
        // Arrange
        String text = "Invalid@middle";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> analyzeSentenceService.analyzeSyntax(text),
                "Text with invalid characters should throw IllegalArgumentException");
        assertEquals("Input text contains invalid characters.", exception.getMessage(),
                "Error message should match expected for invalid characters in the text");
    }

    @Test
    void testAnalyzeSyntax_syntaxTreeGeneration() {
        logger.info("Testing syntax tree generation from valid input...");
        // Arrange
        String text = "Simple sentence";

        try {
            // Gets the API key; throws MissingApiKeyException if not configured.
            ConfigManager.getInstance().getProperty("api.key.file");
        } catch (MissingApiKeyException e) {
            logger.warn("Skipping test due to missing API Key: {}", e.getMessage());
            Assumptions.assumeTrue(false, "Test skipped: API Key is not configured.");
        }
        // Act
        Sentence result = analyzeSentenceService.analyzeSyntax(text);

        // Assert
        assertNotNull(result.getSyntaxTree(), "Syntax tree should be generated for valid text");
    }

    @Test
    void testAnalyzeSyntax_unknownTokenHandling() {
        logger.info("Testing analyzeSyntax() for input containing unknown tokens...");
        // Arrange
        String text = "74c9nywrq9cn";
        try {
            // Gets the API key; throws MissingApiKeyException if not configured.
            ConfigManager.getInstance().getProperty("api.key.file");
        } catch (MissingApiKeyException e) {
            logger.warn("Skipping test due to missing API Key: {}", e.getMessage());
            Assumptions.assumeTrue(false, "Test skipped: API Key is not configured.");
        }
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> analyzeSentenceService.analyzeSyntax(text),
                "Sentence containing unknown token should throw IllegalArgumentException");
        assertTrue(exception.getMessage().contains("Invalid token for grammatical type"),
                "Exception message should indicate invalid token handling");
    }
}