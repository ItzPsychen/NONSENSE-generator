package unipd.edids.logicBusiness.entities;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordTest {

    @BeforeAll
    static void setupAll() {
        System.out.println("Starting WordTest suite...");
    }

    @AfterAll
    static void teardownAll() {
        System.out.println("Finished WordTest suite.");
    }

    abstract static class MockWord extends Word {
        protected MockWord(String filePath) {
            super(filePath);
        }
    }

    @BeforeEach
    void setup() {
        System.out.println("Starting a new test...");
    }

    @AfterEach
    void teardown() {
        System.out.println("Test completed.");
    }

    @Test
    void testGetRandomWordWithEmptyFile() throws IOException {
        File tempFile = File.createTempFile("emptyFile", ".txt");
        tempFile.deleteOnExit();

        Word word = new MockWord(tempFile.getAbsolutePath()) {
        };

        assertEquals("undefined", word.getRandomWord(), "Expected 'undefined' when no words are present.");
    }

    @Test
    void testGetRandomWordWithSingleWord() throws IOException {
        File tempFile = File.createTempFile("singleWord", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("hello\n");
        }

        Word word = new MockWord(tempFile.getAbsolutePath()) {
        };

        assertEquals("hello", word.getRandomWord(), "Expected the only word in the file to be returned.");
    }

    @Test
    void testGetRandomWordWithMultipleWords() throws IOException {
        File tempFile = File.createTempFile("multiWords", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("apple\nbanana\ncherry\n");
        }

        Word word = new MockWord(tempFile.getAbsolutePath()) {
        };
        List<String> validWords = List.of("apple", "banana", "cherry");

        String randomWord = word.getRandomWord();
        assertTrue(validWords.contains(randomWord), "Random word should be one of the words in the file.");
    }

    @Test
    void testOnFileChangedReloadsWords() throws IOException {
        File tempFile = File.createTempFile("reloadTest", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("initial\n");
        }

        Word word = new MockWord(tempFile.getAbsolutePath()) {
        };

        assertEquals("initial", word.getRandomWord(), "Expected 'initial' word to be loaded initially.");

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("updated\n");
        }

        word.onFileChanged(tempFile.getAbsolutePath());
        assertEquals("updated", word.getRandomWord(), "Expected 'updated' word to be loaded after file change.");
    }
}