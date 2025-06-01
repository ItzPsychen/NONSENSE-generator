package unipd.edids.logicBusiness;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.exceptions.AnalyzeException;
import unipd.edids.logicBusiness.exceptions.GenerateException;
import unipd.edids.logicBusiness.exceptions.MissingApiKeyException;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppManagerTest {

    private static final Logger logger = LogManager.getLogger(AppManagerTest.class);
    private static final String TEMP_ANALYZED_FILE_PATH = "temp_analyzed_file.txt";
    private static final String TEMP_GENERATED_FILE_PATH = "temp_generated_file.txt";
    private ConfigManager configManager;
    private AppManager appManager;
    private int testNumber = 0;
    private File tempAnalyzedFile;
    private File tempGeneratedFile;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: AppManagerTest");
        try {
            // Create temporary files for analyzed and generated sentences
            tempAnalyzedFile = new File(TEMP_ANALYZED_FILE_PATH);
            tempGeneratedFile = new File(TEMP_GENERATED_FILE_PATH);
            if (tempAnalyzedFile.createNewFile() && tempGeneratedFile.createNewFile()) {
                logger.info("Temporary files created for testing: {}, {}", TEMP_ANALYZED_FILE_PATH, TEMP_GENERATED_FILE_PATH);
            } else {
                throw new IOException("Failed to create temporary test files.");
            }
        } catch (IOException e) {
            logger.error("Error during setup of test suite: {}", e.getMessage());
            fail("Failed to set up before all tests.");
        }
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
        configManager = ConfigManager.getInstance();
        appManager = new AppManager(configManager);

        configManager.setProperty("analyzed.save.file", tempAnalyzedFile.getAbsolutePath());
        configManager.setProperty("generated.save.file", tempGeneratedFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
        appManager.clearAll(); // Clear any generated state
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: AppManagerTest");
        try {
            if (tempAnalyzedFile.exists() && tempAnalyzedFile.delete()) {
                logger.info("Temporary file deleted: {}", TEMP_ANALYZED_FILE_PATH);
            }
            if (tempGeneratedFile.exists() && tempGeneratedFile.delete()) {
                logger.info("Temporary file deleted: {}", TEMP_GENERATED_FILE_PATH);
            }
            ConfigManager.getInstance().resetDefault();
            logger.info("ConfigManager reset to default.");
        } catch (IOException e) {
            logger.error("Error during cleanup after all tests: {}", e.getMessage());
        }
    }


    @Test
    public void testAnalyzeSentence_SuccessCase() {
        checkApiKey();
        Sentence analyzedSentence = appManager.analyzeSentence("This is a valid test sentence.", false);

        assertNotNull(analyzedSentence, "Analyzed sentence should not be null.");
        assertEquals("This is a valid test sentence.", analyzedSentence.getSentence().toString(), "Sentences should match the input text.");
    }

    @Test
    public void testAnalyzeSentence_SaveToFile() throws IOException {
        checkApiKey();
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
        Exception exception = assertThrows(AnalyzeException.class, () -> appManager.analyzeSentence("", false), "Empty input should throw an AnalyzeException.");

        assertTrue(exception.getMessage().contains("Sentence analysis failed"), "Exception message should contain descriptive text.");
    }

    @Test
    public void testGenerateSentence_SuccessCase() throws IOException {
        checkApiKey();
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
    public void testGenerateSentence_RandomStrategy() {
        checkApiKey();

        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", false, false, false, false);

        assertNotNull(generatedSentence, "Generated sentence with random-strategy should not be null.");
    }

    @Test
    public void testGenerateSentence_SameStrategy() throws IOException {
        checkApiKey();


        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Sentence generatedSentence = appManager.generateSentence("SAME", "simple", false, false, false, false);

        assertNotNull(generatedSentence, "Generated sentence with same-strategy should not be null.");
    }

    @Test
    public void testGenerateSentence_SelectedStrategy() throws IOException {
        checkApiKey();

        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Sentence generatedSentence = appManager.generateSentence("SELECTED", "simple", false, false, false, false);

        assertNotNull(generatedSentence, "Generated sentence with selected-strategy should not be null.");
    }

    @Test
    public void testGenerateSentence_NullStrategy() {
        checkApiKey();
        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Exception exception = assertThrows(GenerateException.class, () -> appManager.generateSentence(null, "simple", false, false, false, false), "Null strategy should throw a GenerateException.");

        assertTrue(exception.getMessage().contains("Sentence generation failed"), "Exception message should indicate the cause due to null strategy.");
    }

    @Test
    public void testGenerateSentence_NullStructureStrategy() {
        checkApiKey();
        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Exception exception = assertThrows(GenerateException.class, () -> appManager.generateSentence("SELECTED", null, false, false, false, false), "Null structure should throw a GenerateException.");

        assertTrue(exception.getMessage().contains("Sentence generation failed"), "Exception message should indicate the cause due to null strategy.");
    }

    @Test
    public void testGenerateSentence_InvalidStrategyParameter() {
        checkApiKey();
        // Mock a valid input sentence
        appManager.analyzeSentence("This is a valid input.", false);

        Exception exception = assertThrows(GenerateException.class, () -> appManager.generateSentence("INVALID", "", false, false, false, false), "Invalid strategy name should throw a GenerateException.");

        assertTrue(exception.getMessage().contains("Sentence generation failed"), "Exception message should contain descriptive text.");
    }

    @Test
    public void testGenerateSentence_InputNotSetted() {
        Exception exception = assertThrows(GenerateException.class, () -> appManager.generateSentence("RANDOM", "", true, false, false, false), "Input Sentence not set throw a GenerateException.");

        assertTrue(exception.getMessage().contains("Sentence generation failed"), "Exception message should contain descriptive text.");
    }

    @Test
    public void testGenerateSentence_WithoutInputSentenceAndNoNewWords() {
        // Do not analyze any sentence before generating

        Exception exception = assertThrows(GenerateException.class, () -> appManager.generateSentence("RANDOM", "simple", false, false, false, false), "Generating sentence without analyzing or new words should throw an IllegalArgumentException.");

        assertTrue(exception.getMessage().contains("Input sentence cannot be null"), "Exception message should indicate missing input sentence.");
    }

    @Test
    public void testGenerateSentence_WithToxicityEnabled() throws IOException {
        checkApiKey();
        // Mock a valid input sentence
//        appManager.analyzeSentence("This is a test input", false);

        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", true, false, true, true);
        assertNotNull(generatedSentence, "Generated sentence with toxicity moderation should not be null.");
    }

    @Test
    public void testGenerateSentence_DifferentCombinations() throws IOException {
        checkApiKey();
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
        checkApiKey();
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
        checkApiKey();
        // Generazione con RANDOM strategy e newWords abilitato
        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", false, false, true, false);

        // Validazioni sul risultato
        assertNotNull(generatedSentence, "Generated sentence should not be null, even without an input sentence.");
    }

    @Test
    public void testClearAll_SuccessCase() {
        checkApiKey();
        // Analyze and generate sentences
        appManager.analyzeSentence("This is a valid input sentence.", false);
        appManager.generateSentence("RANDOM", "simple", false, false, false, false);

        // Clear all
        appManager.clearAll();

        assertNull(appManager.getOutputSentence(), "Output sentence should be null after clearing.");
    }

    @Test
    public void testGetOutputSentence() {
        checkApiKey();
        // Analyze and generate sentences
        appManager.analyzeSentence("This is a valid input sentence.", false);
        Sentence generatedSentence = appManager.generateSentence("RANDOM", "simple", false, false, false, false);

        // Verify the last generated output
        assertEquals(generatedSentence, appManager.getOutputSentence(), "getOutputSentence should return the last generated sentence.");
    }

    protected void checkApiKey() {
        try {
            // Gets the API key; throws MissingApiKeyException if not configured.
            ConfigManager.getInstance().getProperty("api.key.file");
        } catch (
                MissingApiKeyException e) {
            logger.warn("Skipping test due to missing API Key: {}", e.getMessage());
            Assumptions.assumeTrue(false, "Test skipped: API Key is not configured.");
        }

    }

        
}