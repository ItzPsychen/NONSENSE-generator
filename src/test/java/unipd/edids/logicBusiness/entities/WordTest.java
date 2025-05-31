package unipd.edids.logicBusiness.entities;

import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordTest {

    private static final String TEMP_FILE_PREFIX = "test_words";
    private static final String TEMP_FILE_SUFFIX = ".txt";

    static File tempFile;

    static class TestWord extends Word {
        public TestWord(String filePath) {
            super(filePath);
        }
    }

    @BeforeEach
    void setUpAll() throws IOException {
        tempFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
        tempFile.deleteOnExit();

        FileWriter writer = new FileWriter(tempFile);
        writer.write("apple\nbanana\ncherry");
        writer.close();

    }

    @AfterEach
    void tearDownAll() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    @DisplayName("Test getRandomWord returns a word from the list")
    void testGetRandomWordFromList() {
        Word word = new TestWord(tempFile.getAbsolutePath());
        for (int i = 0; i < 100; i++) { // Test multiple times for randomness
            String randomWord = word.getRandomWord();
            assertTrue(List.of("apple", "banana", "cherry").contains(randomWord),
                    "getRandomWord() should return a word from the file but returned: " + randomWord);
        }
    }

    @Test
    @DisplayName("Test getRandomWord returns 'undefined' when no words are loaded")
    void testGetRandomWordUndefinedForEmptyList() throws IOException {
        File emptyTempFile = File.createTempFile("test_empty", ".txt");
        emptyTempFile.deleteOnExit();

        Word word = new TestWord(emptyTempFile.getAbsolutePath());
        assertEquals("undefined", word.getRandomWord(),
                "getRandomWord() should return 'undefined' when there are no words");
    }

    @Test
    @DisplayName("Test getFilePath returns the correct path")
    void testGetFilePath() {
        Word word = new TestWord(tempFile.getAbsolutePath());
        assertEquals(tempFile.getAbsolutePath(), word.getFilePath(),
                "getFilePath() should return the correct file path");
    }

    @Test
    @DisplayName("Test onFileChanged reloads updated words")
    void testOnFileChangedReloadsWords() throws IOException {
        Word word = new TestWord(tempFile.getAbsolutePath());
        assertTrue(List.of("apple", "banana", "cherry").contains(word.getRandomWord()),
                "Random word should initially come from the original file");

        // Update the tempFile content
        FileWriter writer = new FileWriter(tempFile);
        writer.write("dog\nelephant\nfish");
        writer.close();

        word.onFileChanged(tempFile.getAbsolutePath());
        for (int i = 0; i < 100; i++) { // Test again for randomness
            String randomWord = word.getRandomWord();
            assertTrue(List.of("dog", "elephant", "fish").contains(randomWord),
                    "getRandomWord() should return a word from the updated file but returned: " + randomWord);
        }
    }
}