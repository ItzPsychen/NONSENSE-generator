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
public class WordTest {

    private static final Logger logger = LogManager.getLogger(WordTest.class);
    private File tempFile;
    private int testNumber = 0;

    static class TestWord extends Word {
        public TestWord(String filePath) {
            super(filePath);
        }
    }

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: WordTest");
        try {
            tempFile = File.createTempFile("word_test", ".txt");
            tempFile.deleteOnExit();


        } catch (IOException e) {
            logger.error("Failed to create temporary file for WordTest", e);
            fail("Failed to write to temporary file for WordTest");
        }
    }

    @BeforeEach
    void createTempFile() {
        try (FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write("apple\nbanana\ncherry");
        }catch (IOException e) {
            logger.error("Failed to write to temporary file for WordTest", e);
            fail("Failed to write to temporary file for WordTest");
        }
        testNumber++;
        logger.info("Starting test #{}", testNumber);
    }

    @Test
    void testGetRandomWordFromList() {
        Word word = new TestWord(tempFile.getAbsolutePath());
        for (int i = 0; i < 100; i++) { // Test multiple times for randomness
            String randomWord = word.getRandomWord();
            assertTrue(List.of("apple", "banana", "cherry").contains(randomWord),
                    "getRandomWord() should return a word from the file but returned: " + randomWord);
        }
    }

    @Test
    void testGetRandomWordUndefinedForEmptyList() throws IOException {
        File emptyTempFile = File.createTempFile("test_empty", ".txt");
        emptyTempFile.deleteOnExit();

        Word word = new TestWord(emptyTempFile.getAbsolutePath());
        assertEquals("undefined", word.getRandomWord(),
                "getRandomWord() should return 'undefined' when there are no words");
    }

    @Test
    void testGetFilePath() {
        Word word = new TestWord(tempFile.getAbsolutePath());
        assertEquals(tempFile.getAbsolutePath(), word.getFilePath(),
                "getFilePath() should return the correct file path");
    }

    @Test
    void testOnFileChangedReloadsWords() throws IOException {
        Word word = new TestWord(tempFile.getAbsolutePath());
        assertTrue(List.of("apple", "banana", "cherry").contains(word.getRandomWord()),
                "Random word should initially come from the original file");

        // Update the tempFile content
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("dog\nelephant\nfish");
        }

        word.onFileChanged(tempFile.getAbsolutePath());
        for (int i = 0; i < 100; i++) { // Test again for randomness
            String randomWord = word.getRandomWord();
            assertTrue(List.of("dog", "elephant", "fish").contains(randomWord),
                    "getRandomWord() should return a word from the updated file but returned: " + randomWord);
        }
    }

    @AfterEach
    void tearDown()  {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: WordTest");
        if (tempFile.exists()) {
            assertTrue(tempFile.delete(), "Failed to delete the temporary file.");
        }
    }
}

