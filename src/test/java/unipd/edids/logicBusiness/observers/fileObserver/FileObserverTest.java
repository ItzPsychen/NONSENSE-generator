package unipd.edids.logicBusiness.observers.fileObserver;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileObserverTest {

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
    public void setupAll() {
        fileObserver = new TestFileObserver();
    }

    @Test
    public void testOnFileChangedWithValidPath() throws IOException {
        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();

        // Simulate file change
        fileObserver.onFileChanged(tempFile.getAbsolutePath());

        Assertions.assertEquals(tempFile.getAbsolutePath(), fileObserver.getLastModifiedFilePath(),
                "The filePath reported by onFileChanged does not match the expected value.");
    }

    @Test
    public void testOnFileChangedWithEmptyPath() {
        String emptyPath = "";

        // Simulate file change
        fileObserver.onFileChanged(emptyPath);

        Assertions.assertEquals(emptyPath, fileObserver.getLastModifiedFilePath(),
                "The filePath should be empty as provided in the onFileChanged invocation but it does not match.");
    }

    @Test
    public void testOnFileChangedWithNullPath() {
        String nullPath = null;

        // Simulate file change
        fileObserver.onFileChanged(nullPath);

        Assertions.assertNull(fileObserver.getLastModifiedFilePath(),
                "The filePath should be null as provided in the onFileChanged invocation but it does not match.");
    }

    @Test
    public void testOnFileChangedWithLongPath() throws IOException {
        File tempFile = File.createTempFile("longFilePathTest", ".txt");
        tempFile.deleteOnExit();

        StringBuilder longPath = new StringBuilder(tempFile.getAbsolutePath());
        for (int i = 0; i < 500; i++) {
            longPath.append("a"); // Create a long file path
        }

        // Simulate file change
        fileObserver.onFileChanged(longPath.toString());

        Assertions.assertEquals(longPath.toString(), fileObserver.getLastModifiedFilePath(),
                "The filePath reported by onFileChanged does not match the expected long path value.");
    }

    @AfterAll
    public void tearDown() {
        fileObserver = null;
    }
}