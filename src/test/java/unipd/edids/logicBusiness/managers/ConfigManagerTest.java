package unipd.edids.logicBusiness.managers;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

    /**
     * Tests the getInstance method to ensure it returns a non-null instance of ConfigManager.
     */
    @Test
    void testGetInstance_NotNull() {
        ConfigManager instance = ConfigManager.getInstance();
        assertNotNull(instance, "getInstance should return a non-null instance of ConfigManager");
    }

    /**
     * Tests the getInstance method to ensure it returns the same instance of ConfigManager.
     */
    @Test
    void testGetInstance_Singleton() {
        ConfigManager instance1 = ConfigManager.getInstance();
        ConfigManager instance2 = ConfigManager.getInstance();
        assertSame(instance1, instance2, "getInstance should return the same instance of ConfigManager");
    }

    /**
     * Tests that a generic environment variable key is resolved correctly.
     */
    @Test
    void testGetEnv_ValidKey() {
        ConfigManager configManager = ConfigManager.getInstance();
        String envKey = "DEFAULT_CONFIG_FILE_PATH";

        // Assuming an environment variable DEFAULT_CONFIG_FILE_PATH exists in the system for this test to run.
        assertDoesNotThrow(() -> configManager.getEnv(envKey), "getEnv should resolve valid environment keys without errors");
    }

    /**
     * Tests that accessing an invalid (non-existent) environment variable throws an exception.
     */
    @Test
    void testGetEnv_InvalidKey() {
        ConfigManager configManager = ConfigManager.getInstance();
        String invalidKey = "INVALID_ENV_KEY";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> configManager.getEnv(invalidKey),
                "getEnv should throw an exception when accessing a non-existent environment key");
        assertTrue(exception.getMessage().contains(invalidKey), "Exception message should indicate the missing key");
    }

    /**
     * Tests setting and getting a property in the configuration file.
     */
    @Test
    void testSetAndGetProperty() {
        ConfigManager configManager = ConfigManager.getInstance();
        String testKey = "testKey";
        String testValue = "testValue";

        configManager.setProperty(testKey, testValue);
        String retrievedValue = configManager.getProperty(testKey);

        assertEquals(testValue, retrievedValue, "getProperty should return the value set using setProperty");
    }

    /**
     * Tests that accessing a non-existent property throws an exception.
     */
    @Test
    void testGetProperty_InvalidKey() {
        ConfigManager configManager = ConfigManager.getInstance();
        String invalidKey = "INVALID_PROPERTY";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> configManager.getProperty(invalidKey),
                "getProperty should throw an exception when accessing a non-existent property");
        assertTrue(exception.getMessage().contains(invalidKey), "Exception message should indicate the missing property");
    }

    /**
     * Tests that the properties are correctly saved to the configuration file.
     */
    @Test
    void testSaveProperties() {
        ConfigManager configManager = ConfigManager.getInstance();
        String testKey = "testKeySave";
        String testValue = "testValueSave";

        configManager.setProperty(testKey, testValue);
        assertDoesNotThrow(configManager::saveProperties, "saveProperties should not throw an exception");

        // Reload properties to confirm the save operation
        configManager.loadProperties();
        String retrievedValue = configManager.getProperty(testKey);

        assertEquals(testValue, retrievedValue, "Saved property should persist after reload");
    }

    /**
     * Tests resetting the configuration to the default values with a new API key.
     * Creates temp mock files for the test.
     */
    @Test
    void testResetDefault() throws IOException {
        ConfigManager configManager = ConfigManager.getInstance();

        File tempDefaultFile = File.createTempFile("defaultConfig", ".tmp");
        tempDefaultFile.deleteOnExit();

        // Write default properties to the temp file
        String simulatedDefaultConfig = "api.key.file=defaultKey\nanotherKey=anotherValue";
        FileManager.appendLineToSavingFile(tempDefaultFile.getAbsolutePath(), simulatedDefaultConfig);

        File tempConfigFile = new File(configManager.getEnv("CONFIG_FILE_PATH"));
        tempConfigFile.deleteOnExit();

        // Simulate resetting configuration
        String newApiKey = "newApiKey";

        assertDoesNotThrow(() -> configManager.resetDefault(newApiKey), "resetDefault should not throw an exception if default configuration exists");
        assertEquals(newApiKey, configManager.getProperty("api.key.file"), "API key should be updated to the new value after resetting");
    }
}