package unipd.edids.logicBusiness.observers.fileObserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileObserverTest {

    private static final Logger logger = LogManager.getLogger(FileObserverTest.class);
    private int testNumber = 0;

    private static class TestFileObserver implements FileObserver {
        private String lastModifiedFilePath;

        @Override
        public void onFileChanged(String filePath) {
            lastModifiedFilePath = filePath;
        }

        public String getLastModifiedFilePath() {
            return lastModifiedFilePath;
        }
    }

    private TestFileObserver fileObserver;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: FileObserverTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
        fileObserver = new TestFileObserver();
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: FileObserverTest");
    }

    @Test
    void testOnFileChangedWithValidPath() throws IOException {
        logger.info("Testing onFileChanged() with a valid file path...");
        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();

        // Act
        fileObserver.onFileChanged(tempFile.getAbsolutePath());

        // Assert
        assertEquals(tempFile.getAbsolutePath(), fileObserver.getLastModifiedFilePath(),
                "The filePath reported by onFileChanged does not match the expected value.");
    }

    @Test
    void testOnFileChangedWithEmptyPath() {
        logger.info("Testing onFileChanged() with an empty file path...");
        String emptyPath = "";

        // Act
        fileObserver.onFileChanged(emptyPath);

        // Assert
        assertEquals(emptyPath, fileObserver.getLastModifiedFilePath(),
                "The filePath should be empty as provided in the onFileChanged invocation but it does not match.");
    }

    @Test
    void testOnFileChangedWithNullPath() {
        logger.info("Testing onFileChanged() with a null file path...");
        String nullPath = null;

        // Act
        fileObserver.onFileChanged(nullPath);

        // Assert
        assertNull(fileObserver.getLastModifiedFilePath(),
                "The filePath should be null as provided in the onFileChanged invocation but it does not match.");
    }

    @Test
    void testOnFileChangedWithLongPath() throws IOException {
        logger.info("Testing onFileChanged() with a long file path...");
        File tempFile = File.createTempFile("longFilePathTest", ".txt");
        tempFile.deleteOnExit();

        StringBuilder longPath = new StringBuilder(tempFile.getAbsolutePath());
        for (int i = 0; i < 500; i++) {
            longPath.append("a"); // Create a long file path
        }

        // Act
        fileObserver.onFileChanged(longPath.toString());

        // Assert
        assertEquals(longPath.toString(), fileObserver.getLastModifiedFilePath(),
                "The filePath reported by onFileChanged does not match the expected long path value.");
    }
}