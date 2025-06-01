package unipd.edids.logicBusiness.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileManagerTest {

    private static final Logger logger = LogManager.getLogger(FileManagerTest.class);
    private static final String TEST_FILE_PATH = "test_vocabulary_file.txt";
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: FileManagerTest");
    }

    @BeforeEach
    void setUp() throws IOException {
        logger.info("Running test #{}", ++testNumber);

        // Create an empty test file before each test
        File testFile = new File(TEST_FILE_PATH);
        if (!testFile.exists() && !testFile.createNewFile()) {
            fail("Failed to create test file");
        }
    }


    @Test
    void testAppendLineToVocabularyFile_WhenFileExists() throws IOException {
        // Description: Tests appending a line to an existing file
        String lineToAppend = "This is a test line";

        // Act
        FileManager.appendLineToVocabularyFile(TEST_FILE_PATH, lineToAppend);

        // Assert
        List<String> lines = FileManager.readFile(TEST_FILE_PATH);
        assertEquals(1, lines.size(), "File should contain one line after appending");
        assertEquals(lineToAppend, lines.getFirst(), "The appended line does not match expected content");
    }

    @Test
    void testAppendLineToVocabularyFile_WhenFileDoesNotExist() throws IOException {
        // Description: Tests appending a line to a file that doesn't initially exist
        String newFilePath = "non_existing_file.txt";
        String lineToAppend = "This is a new line for a new file";

        try {
            // Act
            FileManager.appendLineToVocabularyFile(newFilePath, lineToAppend);

            // Assert
            List<String> lines = FileManager.readFile(newFilePath);
            assertEquals(1, lines.size(), "File should contain one line after appending");
            assertEquals(lineToAppend, lines.getFirst(), "The appended line does not match expected content");
        } finally {
            // Clean up
            FileManager.deleteFile(newFilePath);
        }
    }

    @Test
    void testAppendLineToVocabularyFile_AppendsMultipleLines() throws IOException {
        // Description: Tests appending multiple lines to the same file
        String firstLine = "First line";
        String secondLine = "Second line";

        // Act
        FileManager.appendLineToVocabularyFile(TEST_FILE_PATH, firstLine);
        FileManager.appendLineToVocabularyFile(TEST_FILE_PATH, secondLine);

        // Assert
        List<String> lines = FileManager.readFile(TEST_FILE_PATH);
        assertEquals(2, lines.size(), "File should contain two lines after appending");
        assertEquals(firstLine, lines.get(0), "The first appended line does not match expected content");
        assertEquals(secondLine, lines.get(1), "The second appended line does not match expected content");
    }

    @Test
    void testAppendLineToVocabularyFile_EmptyLine() throws IOException {
        // Description: Tests appending an empty line to a file
        String emptyLine = "";

        // Act
        FileManager.appendLineToVocabularyFile(TEST_FILE_PATH, emptyLine);

        // Assert
        List<String> lines = FileManager.readFile(TEST_FILE_PATH);
        assertTrue(lines.isEmpty(), "Empty line should not be written to the file");
    }

    @Test
    void testAppendLineToVocabularyFile_IOExceptionThrown() {
        // Description: Tests handling IOException when appending to a file
        String invalidFilePath = "/invalid/path/to/file.txt";
        String lineToAppend = "This should fail";

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> FileManager.appendLineToVocabularyFile(invalidFilePath, lineToAppend), "An IOException should be thrown for invalid file paths");
        assertNotNull(exception.getMessage(), "Exception message should not be null");
    }

    @AfterEach
    void tearDown() throws IOException {
        logger.info("Finished test #{}", testNumber);

        // Clean up and delete test file after each test
        FileManager.deleteFile(TEST_FILE_PATH);
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: FileManagerTest");
    }

}