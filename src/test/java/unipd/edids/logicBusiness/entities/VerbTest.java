package unipd.edids.logicBusiness.entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VerbTest {

    private static final Logger logger = LogManager.getLogger(VerbTest.class);
    private File tempFile;
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: VerbTest");
        try {
            tempFile = File.createTempFile("verb_test", ".txt");
            tempFile.deleteOnExit();




        } catch (IOException e) {
            logger.error("Failed to create temporary file for VerbTest", e);
            fail("Failed to write to temporary file for VerbTest");
        }
        logger.info("Property 'verb.file' configured with path: {}", tempFile.getAbsolutePath());

    }

    @BeforeEach
    void createTempFile() {
        try (FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write("run\njump\ntalk");
        } catch (IOException e) {
            logger.error("Failed to write to temporary file for VerbTest", e);
            fail("Failed to write to temporary file for VerbTest");
        }
        ConfigManager.getInstance().setProperty("verb.file", tempFile.getAbsolutePath());
        testNumber++;
        logger.info("Starting test #{}", testNumber);
    }

    @Test
    void testGetInstanceCreatesSingleton() {
        Verb instance1 = Verb.getInstance();
        Verb instance2 = Verb.getInstance();
        assertNotNull(instance1, "Expected the returned instance to not be null.");
        assertSame(instance1, instance2, "Expected getInstance to return the same instance.");
    }

    @Test
    void testGetInstanceInitializesProperly() {
        assertNotNull(Verb.getInstance().getFilePath(), "Expected filePath to not be null after instantiation.");
        assertEquals(tempFile.getAbsolutePath(), Verb.getInstance().getFilePath(), "FilePath did not match the expected value.");
        assertFalse(Verb.getInstance().words.isEmpty(), "Expected words list to be populated after instantiation.");
    }

    @Test
    void testOnConfigChangeUpdatesFilePathAndReloadsWords() throws IOException {


        File newTempFile = File.createTempFile("verb_test_update", ".txt");
        newTempFile.deleteOnExit();

        try (FileWriter fileWriter = new FileWriter(newTempFile)) {
            fileWriter.write("swim\nclimb\ndrive");
        }
        Verb.getInstance().onConfigChange("verb.file", newTempFile.getAbsolutePath());

        assertEquals(newTempFile.getAbsolutePath(), Verb.getInstance().getFilePath(), "Expected filePath to be updated after config change.");
        assertFalse(Verb.getInstance().words.isEmpty(), "Expected words list to be updated after config change.");
        assertTrue(Verb.getInstance().words.contains("swim"), "Expected words list to contain data from the new file.");
    }

    @Test
    void testVerbLoad() {
        Verb verb = Verb.getInstance();

        assertFalse(verb.words.isEmpty(), "Expected words list to be populated.");
        assertTrue(verb.words.contains("run"), "Expected words list to contain 'run'.");
    }

    @Test
    void testRandomWordRetrieval() {
        Verb verb = Verb.getInstance();
        verb.configureVerbTense(false); // Ensure present tense is default
        String randomWord = verb.getRandomWord();

        assertNotNull(randomWord, "Expected random word to not be null.");
        assertTrue(List.of("run", "jump", "talk").contains(randomWord),
                "Expected random word to be one of the defined words.");
    }


    @Test
    void testConjugateUsesPresentTenseByDefault() {
        Verb verb = Verb.getInstance();
        verb.configureVerbTense(false); // Ensure present tense is default
        String conjugatedWord = verb.conjugate("run");

        assertNotNull(conjugatedWord, "Conjugated word should not be null in present tense");
        assertEquals("run", conjugatedWord, "Conjugated word should be 'run' in present tense");
    }

    @Test
    void testConjugateSwitchesToFutureTense() {
        Verb verb = Verb.getInstance();
        verb.configureVerbTense(true); // Switch to future tense
        String conjugatedWord = verb.conjugate("jump");

        assertNotNull(conjugatedWord, "Conjugated word should not be null in future tense");
        assertEquals("will jump", conjugatedWord, "Conjugated word should be 'will jump' in future tense");
    }


    @AfterEach
    void tearDown()  {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() throws IOException {
        logger.info("Finished test suite: VerbTest");
        ConfigManager.getInstance().resetDefault();
        if (tempFile.exists()) {
            assertTrue(tempFile.delete(), "Failed to delete the temporary file.");
        }
    }
}