package unipd.edids.logicBusiness.entities;

import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdjectiveTest {

    private File tempFile;

    @BeforeEach
    void createTempFile() throws IOException {
        tempFile = File.createTempFile("adjective_test", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write("big\nsmall\nquick");
        }

        ConfigManager.getInstance().setProperty("adjective.file", tempFile.getAbsolutePath());
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

        ConfigManager.getInstance().setProperty("adjective.file", newTempFile.getAbsolutePath());

        assertEquals(newTempFile.getAbsolutePath(), instance.getFilePath(), "Expected filePath to be updated after config change.");
        assertFalse(instance.words.isEmpty(), "Expected words list to be updated after config change.");
        assertTrue(instance.words.contains("fast"), "Expected words list to contain data from the new file.");
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