package unipd.edids.logicBusiness.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigManagerTest {

    private static final Logger logger = LogManager.getLogger(ConfigManagerTest.class);
    private int testNumber = 0;
    private static final String TEST_KEY = "testKey";
    private static final String TEST_VALUE = "testValue";

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: ConfigManagerTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: ConfigManagerTest");
    }

    @Test
    void testGetInstance_NotNull() {
        logger.info("Testing getInstance() returns non-null...");
        ConfigManager instance = ConfigManager.getInstance();
        assertNotNull(instance, "getInstance() should return a non-null instance of ConfigManager");
    }

    @Test
    void testGetInstance_Singleton() {
        logger.info("Testing getInstance() returns the same instance...");
        ConfigManager instance1 = ConfigManager.getInstance();
        ConfigManager instance2 = ConfigManager.getInstance();
        assertSame(instance1, instance2, "getInstance() should return the same ConfigManager instance");
    }

    @Test
    void testGetEnv_ValidKey() {
        logger.info("Testing getEnv() with a valid key...");
        String envKey = "DEFAULT_CONFIG_FILE_PATH";

        assertDoesNotThrow(() -> ConfigManager.getInstance().getEnv(envKey), "getEnv() should resolve valid environment keys without errors");
    }

    @Test
    void testGetEnv_InvalidKey() {
        logger.info("Testing getEnv() with an invalid key...");

        String invalidKey = "INVALID_ENV_KEY";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> ConfigManager.getInstance().getEnv(invalidKey),
                "getEnv() should throw an exception when accessing a non-existent environment key");
        assertTrue(exception.getMessage().contains(invalidKey), "Exception message should indicate the missing key");
    }

    @Test
    void testSetAndGetProperty() {
        logger.info("Testing setProperty() and getProperty()...");


        ConfigManager.getInstance().setProperty(TEST_KEY, TEST_VALUE);
        String retrievedValue = ConfigManager.getInstance().getProperty(TEST_KEY);

        assertEquals(TEST_VALUE, retrievedValue, "getProperty() should return the value set using setProperty()");
    }

    @Test
    void testGetProperty_InvalidKey() {
        logger.info("Testing getProperty() with an invalid key...");
        String invalidKey = "INVALID_PROPERTY";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> ConfigManager.getInstance().getProperty(invalidKey),
                "getProperty() should throw an exception when accessing a non-existent property");
        assertTrue(exception.getMessage().contains(invalidKey), "Exception message should indicate the missing property");
    }

    @Test
    void testSaveProperties() {
        logger.info("Testing saveProperties()...");

        String testKey = "testKeySave";
        String testValue = "testValueSave";

        ConfigManager.getInstance().setProperty(testKey, testValue);
        assertDoesNotThrow(ConfigManager.getInstance()::saveProperties, "saveProperties() should not throw an exception");

        // Reloading properties to confirm persistence
        ConfigManager.getInstance().loadProperties();
        String retrievedValue = ConfigManager.getInstance().getProperty(testKey);

        assertEquals(testValue, retrievedValue, "Saved property should persist after reload");
    }

    @Test
    void testResetDefault() throws IOException {
        logger.info("Testing resetDefault()...");
        String themeKey = "ui.theme";
        String themeValue = "dark";
        ConfigManager.getInstance().setProperty(themeKey, themeValue);
        String oldApiKey;
        try {
            oldApiKey = ConfigManager.getInstance().getProperty("api.key.file");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to read API key from config file", e);
            oldApiKey = "";
        }

        assertEquals(themeValue, ConfigManager.getInstance().getProperty(themeKey), "resetDefault() should not change the API key");

        ConfigManager.getInstance().resetDefault();
        String newApiKey;
        try {
            newApiKey = ConfigManager.getInstance().getProperty("api.key.file");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to read API key from config file", e);
            newApiKey = "";
        }
        assertEquals(newApiKey, oldApiKey, "resetDefault() should update the API key with the old value");
        assertNotEquals(themeValue, ConfigManager.getInstance().getProperty(themeKey), "resetDefault() should update the API key with the old value");

    }
}