package unipd.edids.logicBusiness;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.exceptions.AnalyzeException;
import unipd.edids.logicBusiness.exceptions.GenerateException;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class AppManagerTest {

    private ConfigManager configManager;
    private AppManager appManager;

    @BeforeEach
    public void setUp() {
        configManager = ConfigManager.getInstance();
        appManager = new AppManager(configManager);

        // Mocking configuration properties for test purposes
        configManager.setProperty("analyzed.save.file", "testFile.txt");
        configManager.setProperty("generated.save.file", "testFile.txt");
    }

    @AfterEach
    public void tearDown() {
        File file = new File("testFile.txt");
        if (file.exists()) {
            file.delete();
        }
    }
    @Test
    public void testAnalyzeSentence_SuccessCase() {
        Sentence analyzedSentence = appManager.analyzeSentence("This is a valid test sentence.", false);

        assertNotNull(analyzedSentence, "Analyzed sentence should not be null.");
        assertEquals("This is a valid test sentence.", analyzedSentence.getSentence().toString(), "Sentences should match the input text.");
    }


    @Test
    public void testAnalyzeSentence_SaveToFile() throws IOException {
        // Create temporary file and configure the path in ConfigManager
        File tempFile = File.createTempFile("analyzed_sentence_test", ".txt");
        tempFile.deleteOnExit();
        configManager.setProperty("analyzed.save.file", tempFile.getAbsolutePath());

        Sentence analyzedSentence = appManager.analyzeSentence("This is another test sentence.", true);

        assertNotNull(analyzedSentence, "Analyzed sentence should not be null.");
        assertTrue(tempFile.exists(), "File for saving analyzed sentence should exist.");
        assertTrue(tempFile.length() > 0, "File for saving analyzed sentence should not be empty.");
    }

    @Test
    public void testAnalyzeSentence_InvalidInput() {
        Exception exception = assertThrows(AnalyzeException.class, () -> appManager.analyzeSentence("", false),
            "Empty input should throw an AnalyzeException.");

        assertTrue(exception.getMessage().contains("Sentence analysis failed"), "Exception message should contain descriptive text.");
    }

    @Test
    public void testGenerateSentence_SuccessCase() throws IOException {
        // Create temporary file to save the generated sentence
        File tempFile = File.createTempFile("generated_sentence_test", ".txt");
        tempFile.deleteOnExit();
        configManager.setProperty("generated.save.file", tempFile.getAbsolutePath());

        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", false, false, false, true);

        assertNotNull(generatedSentence, "Generated sentence should not be null.");
        assertTrue(tempFile.exists(), "File for saving generated sentence should exist.");
        assertTrue(tempFile.length() > 0, "File for saving generated sentence should not be empty.");
    }


    @Test
    public void testGenerateSentence_RandomStrategy()  {


        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", false, false, false, false);

        assertNotNull(generatedSentence, "Generated sentence with random-strategy should not be null.");
       }

    @Test
    public void testGenerateSentence_SameStrategy() throws IOException {


        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Sentence generatedSentence = appManager.generateSentence("SAME", "simple", false, false, false, false);

        assertNotNull(generatedSentence, "Generated sentence with same-strategy should not be null.");
           }

    @Test
    public void testGenerateSentence_SelectedStrategy() throws IOException {


        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Sentence generatedSentence = appManager.generateSentence("SELECTED", "simple", false, false, false, false);

        assertNotNull(generatedSentence, "Generated sentence with selected-strategy should not be null.");
       }
    @Test
    public void testGenerateSentence_NullStrategy() {
        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Exception exception = assertThrows(GenerateException.class,
                () -> appManager.generateSentence(null, "simple", false, false, false, false),
                "Null strategy should throw a GenerateException.");

        assertTrue(exception.getMessage().contains("Sentence generation failed"),
                "Exception message should indicate the cause due to null strategy.");
    }
    @Test
    public void testGenerateSentence_NullStructureStrategy() {
        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Exception exception = assertThrows(GenerateException.class,
                () -> appManager.generateSentence("SELECTED", null, false, false, false, false),
                "Null structure should throw a GenerateException.");

        assertTrue(exception.getMessage().contains("Sentence generation failed"),
                "Exception message should indicate the cause due to null strategy.");
    }

    @Test
    public void testGenerateSentence_InvalidStrategyParameter() {
        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Exception exception = assertThrows(GenerateException.class,
            () -> appManager.generateSentence("INVALID", "", false, false, false, false),
            "Invalid strategy name should throw a GenerateException.");

        assertTrue(exception.getMessage().contains("Sentence generation failed"), "Exception message should contain descriptive text.");
    }

    @Test
    public void testGenerateSentence_InputNotSetted() {
        Exception exception = assertThrows(GenerateException.class,
                () -> appManager.generateSentence("RANDOM", "", true, false, false, false),
                "Input Sentence not set throw a GenerateException.");

        assertTrue(exception.getMessage().contains("Sentence generation failed"), "Exception message should contain descriptive text.");
    }

    @Test
    public void testGenerateSentence_WithoutInputSentenceAndNoNewWords() {
        // Do not analyze any sentence before generating

        Exception exception = assertThrows(GenerateException.class,
                () -> appManager.generateSentence("RANDOM", "simple", false, false, false, false),
                "Generating sentence without analyzing or new words should throw an IllegalArgumentException.");

        assertTrue(exception.getMessage().contains("Input sentence cannot be null"),
                "Exception message should indicate missing input sentence.");
    }

    @Test
    public void testGenerateSentence_WithToxicityEnabled() throws IOException {
        // Mock a valid input sentence
        appManager.analyzeSentence("This is a test input.", false);

        File tempFile = File.createTempFile("generated_sentence_test_toxicity", ".txt");
        tempFile.deleteOnExit();
        configManager.setProperty("generated.save.file", tempFile.getAbsolutePath());

        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", true, false, false, true);

        assertNotNull(generatedSentence, "Generated sentence with toxicity moderation should not be null.");
        assertTrue(tempFile.exists(), "File generated with toxicity moderation should exist.");
        assertTrue(tempFile.length() > 0, "File generated with toxicity moderation should not be empty.");
    }

    @Test
    public void testGenerateSentence_DifferentCombinations() throws IOException {
        appManager.analyzeSentence("This is a valid input.", false);

        File tempFile1 = File.createTempFile("generated_sentence_test_1", ".txt");
        tempFile1.deleteOnExit();
        configManager.setProperty("generated.save.file", tempFile1.getAbsolutePath());

        // Combination 1: Toxicity and future tense enabled
        Sentence generatedSentence1 = appManager.generateSentence("RANDOM", "simple", true, true, false, true);
        assertNotNull(generatedSentence1, "Generated sentence with toxicity and future tense should not be null.");
        assertTrue(tempFile1.exists(), "Generated file with toxicity and future tense should exist.");
        assertTrue(tempFile1.length() > 0, "Generated file with toxicity and future tense should not be empty.");

        File tempFile2 = File.createTempFile("generated_sentence_test_2", ".txt");
        tempFile2.deleteOnExit();
        configManager.setProperty("generated.save.file", tempFile2.getAbsolutePath());

        // Combination 2: Only newWords enabled
        Sentence generatedSentence2 = appManager.generateSentence("RANDOM", "simple", false, false, true, true);
        assertNotNull(generatedSentence2, "Generated sentence with newWords should not be null.");
        assertTrue(tempFile2.exists(), "Generated file with newWords should exist.");
        assertTrue(tempFile2.length() > 0, "Generated file with newWords should not be empty.");
    }


    @Test
    public void testGenerateSentence_WithFutureTense() throws IOException {
        appManager.analyzeSentence("This is a valid input sentence.", false);

        File tempFile = File.createTempFile("generated_sentence_future", ".txt");
        tempFile.deleteOnExit();
        configManager.setProperty("generated.save.file", tempFile.getAbsolutePath());

        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", false, true, false, true);

        assertNotNull(generatedSentence, "Generated sentence with future tense should not be null.");
        assertTrue(tempFile.exists(), "File for sentence with future tense should exist.");
        assertTrue(tempFile.length() > 0, "File for sentence with future tense should not be empty.");
    }
    @Test
    public void testGenerateSentence_NoInputSentence_RandomStrategy_NewWordsEnabled() throws IOException {

        // Generazione con RANDOM strategy e newWords abilitato
        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", false, false, true, false);

        // Validazioni sul risultato
        assertNotNull(generatedSentence, "Generated sentence should not be null, even without an input sentence.");
    }

    @Test
    public void testClearAll_SuccessCase() {
        // Analyze and generate sentences
        appManager.analyzeSentence("This is a valid input sentence.", false);
        appManager.generateSentence("RANDOM", "simple", false, false, false, false);

        // Clear all
        appManager.clearAll();

        assertNull(appManager.getOutputSentence(), "Output sentence should be null after clearing.");
    }



    @Test
    public void testGetOutputSentence() {
        // Analyze and generate sentences
        appManager.analyzeSentence("This is a valid input sentence.", false);
        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", false, false, false, false);

        // Verify the last generated output
        assertEquals(generatedSentence, appManager.getOutputSentence(), "getOutputSentence should return the last generated sentence.");
    }
}