package unipd.edids.logicBusiness.entities;

import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NounTest {

    private File tempFile;

    @BeforeEach
    void createTempFile() throws IOException {
        tempFile = File.createTempFile("noun_test", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write("apple\nbanana\ncherry");
        }

        ConfigManager.getInstance().setProperty("noun.file", tempFile.getAbsolutePath());
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
            fileWriter.write("dog\ncat\ntiger");
        }

        ConfigManager.getInstance().setProperty("noun.file", newTempFile.getAbsolutePath());

        assertEquals(newTempFile.getAbsolutePath(), instance.getFilePath(), "Expected filePath to be updated after config change.");
        assertFalse(instance.words.isEmpty(), "Expected words list to be updated after config change.");
        assertTrue(instance.words.contains("dog"), "Expected words list to contain data from the new file.");
    }

    @AfterEach
    void tearDown() throws IOException {
        ConfigManager.getInstance().resetDefault(ConfigManager.getInstance().getProperty("api.key.file"));
    }

    @AfterAll
    void cleanUp() {
        if (tempFile.exists()) {
            assertTrue(tempFile.delete(), "Failed to delete the temporary file.");
        }
    }
}