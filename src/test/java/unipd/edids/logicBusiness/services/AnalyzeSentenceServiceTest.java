package unipd.edids.logicBusiness.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unipd.edids.logicBusiness.AppManager;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.services.AnalyzeSentenceService;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AnalyzeSentenceServiceTest {


    private ConfigManager configManager;

    @BeforeEach
    public void setUp() {
        configManager = ConfigManager.getInstance();

    }

    @AfterEach
    public void tearDown() throws IOException {
        configManager.resetDefault(configManager.getProperty("api.key.file"));
        File file = new File("testFile.txt");
        if (file.exists()) {
            file.delete();
        }
    }


    @Test
    void testAnalyzeSyntax_validInput() {
        AnalyzeSentenceService service = new AnalyzeSentenceService();
        String text = "The quick brown fox jumps over the lazy dog";

        Sentence result = service.analyzeSyntax(text);

        assertNotNull(result, "Resulting sentence should not be null");
        assertEquals(text, result.getSentence().toString(),
                "Sentence content should match the input text");
        assertFalse(result.getNouns().isEmpty(), "Nouns list should not be empty");
        assertFalse(result.getVerbs().isEmpty(), "Verbs list should not be empty");
        assertFalse(result.getAdjectives().isEmpty(), "Adjectives list should not be empty");
        assertNotNull(result.getSyntaxTree(), "Syntax tree should not be null");
    }

    @Test
    void testAnalyzeSyntax_emptyInput() {
        AnalyzeSentenceService service = new AnalyzeSentenceService();
        String text = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.analyzeSyntax(text), "Empty text should throw IllegalArgumentException");

        assertEquals("Input text cannot be null or empty.", exception.getMessage(),
                "Error message should match the expected text");
    }

    @Test
    void testAnalyzeSyntax_nullInput() {
        AnalyzeSentenceService service = new AnalyzeSentenceService();
        String text = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.analyzeSyntax(text), "Null text should throw IllegalArgumentException");

        assertEquals("Input text cannot be null or empty.", exception.getMessage(),
                "Error message should match the expected text");
    }

    @Test
    void testAnalyzeSyntax_exceedsMaxLength() throws IOException {
        // Temporarily create and set config file for max length
       configManager.setProperty("max.sentence.length", "10");

        AnalyzeSentenceService service = new AnalyzeSentenceService();
        String text = "This text exceeds the maximum allowed length";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.analyzeSyntax(text),
                "Text exceeding max length should throw IllegalArgumentException");

        assertEquals("Input text cannot exceed 10 characters.", exception.getMessage(),
                "Error message should reflect the exceeded maximum length");
    }

    @Test
    void testAnalyzeSyntax_invalidCharactersStart() {
        AnalyzeSentenceService service = new AnalyzeSentenceService();
        String text = "@Invalid start";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.analyzeSyntax(text),
                "Text with invalid start character should throw IllegalArgumentException");

        assertEquals("Input text contains invalid characters at the start of the text.",
                exception.getMessage(),
                "Error message should match expected for invalid start characters");
    }

    @Test
    void testAnalyzeSyntax_invalidCharactersEnd() {
        AnalyzeSentenceService service = new AnalyzeSentenceService();
        String text = "Valid text%";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.analyzeSyntax(text),
                "Text with invalid end character should throw IllegalArgumentException");

        assertEquals("Input text contains invalid characters at the end of the text.",
                exception.getMessage(),
                "Error message should match expected for invalid end characters");
    }

    @Test
    void testAnalyzeSyntax_invalidCharactersMid() {
        AnalyzeSentenceService service = new AnalyzeSentenceService();
        String text = "Invalid@middle";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.analyzeSyntax(text),
                "Text with invalid characters should throw IllegalArgumentException");

        assertEquals("Input text contains invalid characters.", exception.getMessage(),
                "Error message should match expected for invalid characters in the text");
    }

    @Test
    void testAnalyzeSyntax_syntaxTreeGeneration() {
        AnalyzeSentenceService service = new AnalyzeSentenceService();
        String text = "Simple sentence";

        Sentence result = service.analyzeSyntax(text);

        assertNotNull(result.getSyntaxTree(),
                "Syntax tree should be generated for valid text");
    }

    @Test
    void testAnalyzeSyntax_unknownTokenHandling() {
        AnalyzeSentenceService service = new AnalyzeSentenceService();
        String text = "74c9nywrq9cn";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.analyzeSyntax(text),
                "Sentence containing unknown token should throw IllegalArgumentException");

        assertTrue(exception.getMessage().contains("Invalid token for grammatical type"),
                "Exception message should indicate invalid token handling");
    }
}