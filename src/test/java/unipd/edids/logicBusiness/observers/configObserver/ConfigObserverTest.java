package unipd.edids.logicBusiness.observers.configObserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite per la classe ConfigObserver.
 * Controlla il comportamento delle implementazioni dell'interfaccia ConfigObserver, in particolare
 * il metodo onConfigChange.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigObserverTest {

    private static final Logger logger = LogManager.getLogger(ConfigObserverTest.class);
    private int testNumber = 0;

    private static class MockConfigObserver implements ConfigObserver {
        private final Map<String, String> configMap = new HashMap<>();

        @Override
        public void onConfigChange(String key, String value) {
            if (key == null || value == null) {
                throw new IllegalArgumentException("Key and value cannot be null");
            }
            configMap.put(key, value);
        }

        public String getValue(String key) {
            return configMap.get(key);
        }
    }

    private MockConfigObserver mockConfigObserver;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: ConfigObserverTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
        mockConfigObserver = new MockConfigObserver();
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: ConfigObserverTest");
    }

    @Test
    void testOnConfigChange_WithValidInput_ShouldUpdateConfigMap() {
        logger.info("Testing onConfigChange() with valid input...");
        String key = "key1";
        String value = "value1";

        // Act
        mockConfigObserver.onConfigChange(key, value);

        // Assert
        String actualValue = mockConfigObserver.getValue(key);
        assertEquals(value, actualValue, "The value retrieved from the config map should match the value provided.");
    }

    @Test
    void testOnConfigChange_WithNullKey_ShouldThrowException() {
        logger.info("Testing onConfigChange() with null key...");
        String value = "value1";

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> mockConfigObserver.onConfigChange(null, value),
                "An IllegalArgumentException should be thrown when the key is null.");

        assertEquals("Key and value cannot be null", exception.getMessage(),
                "The exception message should indicate that neither key nor value can be null.");
    }

    @Test
    void testOnConfigChange_WithNullValue_ShouldThrowException() {
        logger.info("Testing onConfigChange() with null value...");
        String key = "key1";

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> mockConfigObserver.onConfigChange(key, null),
                "An IllegalArgumentException should be thrown when the value is null.");

        assertEquals("Key and value cannot be null", exception.getMessage(),
                "The exception message should indicate that neither key nor value can be null.");
    }

    @Test
    void testOnConfigChange_WithSpecialCharactersInKeyAndValue_ShouldUpdateConfigMap() {
        logger.info("Testing onConfigChange() with special characters in key and value...");
        String key = "@specialKey#";
        String value = "!specialValue$";

        // Act
        mockConfigObserver.onConfigChange(key, value);

        // Assert
        String actualValue = mockConfigObserver.getValue(key);
        assertEquals(value, actualValue,
                "The value retrieved from the config map with special characters should match the provided value.");
    }

    @Test
    void testOnConfigChange_WithDuplicateKey_ShouldUpdateValue() {
        logger.info("Testing onConfigChange() with duplicate key...");
        String key = "key1";
        String firstValue = "value1";
        String secondValue = "value2";

        // Act
        mockConfigObserver.onConfigChange(key, firstValue);
        mockConfigObserver.onConfigChange(key, secondValue);

        // Assert
        String actualValue = mockConfigObserver.getValue(key);
        assertEquals(secondValue, actualValue,
                "The value associated with a duplicate key should be updated to the most recent value.");
    }
}