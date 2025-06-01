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
public class AdjectiveTest {

    private static final Logger logger = LogManager.getLogger(AdjectiveTest.class);
    private File tempFile;
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: AdjectiveTest");
        try {
            tempFile = File.createTempFile("adjective_test", ".txt");
            tempFile.deleteOnExit();

        } catch (IOException e) {
            logger.error("Failed to create temporary file for AdjectiveTest", e);
            fail("Failed to write to temporary file for AdjectiveTest");
        }
    }

    @BeforeEach
    void createTempFile() {
        try (FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write("big\nsmall\nquick");
        } catch (IOException e) {
            logger.error("Failed to write to temporary file for AdjectiveTest", e);
            fail("Failed to write to temporary file for AdjectiveTest");
        }
        ConfigManager.getInstance().setProperty("adjective.file", tempFile.getAbsolutePath());

        testNumber++;
        logger.info("Starting test #{}", testNumber);


    }

    @Test
    void testGetInstanceCreatesSingleton() {
        Adjective instance1 = Adjective.getInstance();
        Adjective instance2 = Adjective.getInstance();
        assertNotNull(instance1, "Expected the returned instance to not be null.");
        assertSame(instance1, instance2, "Expected getInstance to return the same instance.");
    }

    @Test
    void testGetInstanceInitializesProperly() {
        Adjective instance = Adjective.getInstance();
        assertNotNull(instance.getFilePath(), "Expected filePath to not be null after instantiation.");
        assertEquals(tempFile.getAbsolutePath(), instance.getFilePath(), "FilePath did not match the expected value.");
        assertFalse(instance.words.isEmpty(), "Expected words list to be populated after instantiation.");
    }

    @Test
    void testOnConfigChangeUpdatesFilePathAndReloadsWords() throws IOException {
        Adjective instance = Adjective.getInstance();

        File newTempFile = File.createTempFile("adjective_test_update", ".txt");
        newTempFile.deleteOnExit();

        try (FileWriter fileWriter = new FileWriter(newTempFile)) {
            fileWriter.write("fast\nslow\nbright");
        }

        Adjective.getInstance().onConfigChange("adjective.file", newTempFile.getAbsolutePath());

        assertEquals(newTempFile.getAbsolutePath(), instance.getFilePath(), "Expected filePath to be updated after config change.");
        assertFalse(instance.words.isEmpty(), "Expected words list to be updated after config change.");
        assertTrue(instance.words.contains("fast"), "Expected words list to contain data from the new file.");
    }


    @Test
    void testAdjectiveLoad() {
        Adjective adjective = Adjective.getInstance();

        assertFalse(adjective.words.isEmpty(), "Expected words list to be populated.");
        assertTrue(adjective.words.contains("big"), "Expected words list to contain 'big'.");
    }

    @Test
    void testRandomWordRetrieval() {
        Adjective adjective = Adjective.getInstance();
        String randomWord = adjective.getRandomWord();
        assertNotNull(randomWord, "Expected random word to not be null.");
        assertTrue(List.of("big", "small", "quick").contains(randomWord),
                "Expected random word to be one of the defined words.");

    }

    @AfterEach
    void tearDown() throws IOException {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() throws IOException {
        logger.info("Finished test suite: AdjectiveTest");
        ConfigManager.getInstance().resetDefault();
        if (tempFile.exists()) {
            assertTrue(tempFile.delete(), "Failed to delete the temporary file.");
        }
    }
}