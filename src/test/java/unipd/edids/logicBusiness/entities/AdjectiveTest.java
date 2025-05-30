package unipd.edids.logicBusiness.entities;

import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AdjectiveTest {

    private static Path tempFilePath;

    @BeforeAll
    static void setup() throws IOException {
        tempFilePath = Files.createTempFile("adjective", ".txt");
        ConfigManager.getInstance().setProperty("adjective.file", tempFilePath.toString());
    }

    @AfterAll
    static void cleanup() throws IOException {
        Files.deleteIfExists(tempFilePath);
    }

    @AfterEach
    void clearSingleton() {
        // Resetting Singleton for independent tests
        try {
            var instance = Adjective.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            fail("Failed to reset singleton: " + e.getMessage());
        }
    }

    @Test
    void testOnConfigChange_FilePathUpdated() throws IOException {
        // Arrange
        Adjective adjective = Adjective.getInstance();
        String newTempFilePath = Files.createTempFile("new_adjective", ".txt").toString();
        try (FileWriter writer = new FileWriter(newTempFilePath)) {
            writer.write("");
        }
        

        // Act
        adjective.onConfigChange("adjective.file", newTempFilePath);

        // Assert
        assertEquals(newTempFilePath, adjective.getFilePath(), "The file path must be updated after the configuration change");

        // Cleanup
        Files.deleteIfExists(Path.of(newTempFilePath));
    }

    @Test
    void testOnConfigChange_NoEffectOnDifferentKey() {
        // Arrange
        Adjective adjective = Adjective.getInstance();
        String originalFilePath = adjective.getFilePath();

        // Act
        adjective.onConfigChange("unrelated.key", "new_value");

        // Assert
        assertEquals(originalFilePath, adjective.getFilePath(), "The file path must not change when an unrelated key is updated");
    }

    @Test
    void testSingletonInstance() {
        // Act
        Adjective instance1 = Adjective.getInstance();
        Adjective instance2 = Adjective.getInstance();

        // Assert
        assertNotNull(instance1, "Singleton instance must not be null");
        assertSame(instance1, instance2, "Singleton instance must be the same across calls");
    }
}