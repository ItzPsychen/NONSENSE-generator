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
public class NounTest {

    private static final Logger logger = LogManager.getLogger(NounTest.class);
    private File tempFile;
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: NounTest");
        try {
            tempFile = File.createTempFile("noun_test", ".txt");
            tempFile.deleteOnExit();

        } catch (IOException e) {
            logger.error("Failed to create temporary file for NounTest", e);
        }
    }

    @BeforeEach
    void createTempFile() {
        try (FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write("cat\ndog\nbird");
        } catch (IOException e) {
            logger.error("Failed to write to temporary file for NounTest", e);
            fail("Failed to write to temporary file for NounTest");
        }
        ConfigManager.getInstance().setProperty("noun.file", tempFile.getAbsolutePath());

        testNumber++;
        logger.info("Starting test #{}", testNumber);
    }

    @Test
    void testGetInstanceCreatesSingleton() {
        Noun instance1 = Noun.getInstance();
        Noun instance2 = Noun.getInstance();
        assertNotNull(instance1, "Expected the returned instance to not be null.");
        assertSame(instance1, instance2, "Expected getInstance to return the same instance.");
    }

    @Test
    void testGetInstanceInitializesProperly() {
        Noun instance = Noun.getInstance();
        assertNotNull(instance.getFilePath(), "Expected filePath to not be null after instantiation.");
        assertEquals(tempFile.getAbsolutePath(), instance.getFilePath(), "FilePath did not match the expected value.");
        assertFalse(instance.words.isEmpty(), "Expected words list to be populated after instantiation.");
    }

    @Test
    void testOnConfigChangeUpdatesFilePathAndReloadsWords() throws IOException {
        Noun instance = Noun.getInstance();

        File newTempFile = File.createTempFile("noun_test_update", ".txt");
        newTempFile.deleteOnExit();

        try (FileWriter fileWriter = new FileWriter(newTempFile)) {
            fileWriter.write("tree\nflower\nmountain");
        }

        Noun.getInstance().onConfigChange("noun.file", newTempFile.getAbsolutePath());

        assertEquals(newTempFile.getAbsolutePath(), instance.getFilePath(), "Expected filePath to be updated after config change.");
        assertFalse(instance.words.isEmpty(), "Expected words list to be updated after config change.");
        assertTrue(instance.words.contains("tree"), "Expected words list to contain data from the new file.");
    }

    @Test
    void testNounLoad() {
        Noun noun = Noun.getInstance();

        assertFalse(noun.words.isEmpty(), "Expected words list to be populated.");
        assertTrue(noun.words.contains("cat"), "Expected words list to contain 'cat'.");
    }

    @Test
    void testRandomWordRetrieval() {
        Noun noun = Noun.getInstance();
        String randomWord = noun.getRandomWord();
        assertNotNull(randomWord, "Expected random word to not be null.");
        assertTrue(List.of("cat", "dog", "bird").contains(randomWord),
                "Expected random word to be one of the defined words.");
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() throws IOException {
        logger.info("Finished test suite: NounTest");
        ConfigManager.getInstance().resetDefault();
        if (tempFile.exists()) {
            assertTrue(tempFile.delete(), "Failed to delete the temporary file.");
        }
    }
}