package unipd.edids.logicBusiness.observers.configObserver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for ConfigObserver interface.
 * The `onConfigChange` method takes a key-value pair as parameters. When invoked, it handles changes
 * in configuration based on the provided key and value. These tests simulate basic behavior of classes
 * implementing the `ConfigObserver` interface.
 */
class ConfigObserverTest {

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
    static void setUpAll() {
        System.out.println("Starting ConfigObserverTest...");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test executed.");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("ConfigObserverTest completed.");
    }

    @Test
    void testOnConfigChange_WithValidInput_ShouldUpdateConfigMap() {
        mockConfigObserver = new MockConfigObserver();
        String key = "key1";
        String value = "value1";

        mockConfigObserver.onConfigChange(key, value);

        String actualValue = mockConfigObserver.getValue(key);
        assertEquals(value, actualValue, "The value retrieved from the config map should match the value provided.");
    }

    @Test
    void testOnConfigChange_WithNullKey_ShouldThrowException() {
        mockConfigObserver = new MockConfigObserver();
        String value = "value1";

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> mockConfigObserver.onConfigChange(null, value),
                "An IllegalArgumentException should be thrown when the key is null.");

        assertEquals("Key and value cannot be null", exception.getMessage(), "The exception message should indicate that neither key nor value can be null.");
    }

    @Test
    void testOnConfigChange_WithNullValue_ShouldThrowException() {
        mockConfigObserver = new MockConfigObserver();
        String key = "key1";

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> mockConfigObserver.onConfigChange(key, null),
                "An IllegalArgumentException should be thrown when the value is null.");

        assertEquals("Key and value cannot be null", exception.getMessage(), "The exception message should indicate that neither key nor value can be null.");
    }

    @Test
    void testOnConfigChange_WithSpecialCharactersInKeyAndValue_ShouldUpdateConfigMap() {
        mockConfigObserver = new MockConfigObserver();
        String key = "@specialKey#";
        String value = "!specialValue$";

        mockConfigObserver.onConfigChange(key, value);

        String actualValue = mockConfigObserver.getValue(key);
        assertEquals(value, actualValue, "The value retrieved from the config map with special characters should match the provided value.");
    }

    @Test
    void testOnConfigChange_WithDuplicateKey_ShouldUpdateValue() {
        mockConfigObserver = new MockConfigObserver();
        String key = "key1";
        String firstValue = "value1";
        String secondValue = "value2";

        mockConfigObserver.onConfigChange(key, firstValue);
        mockConfigObserver.onConfigChange(key, secondValue);

        String actualValue = mockConfigObserver.getValue(key);
        assertEquals(secondValue, actualValue, "The value associated with a duplicate key should be updated to the most recent value.");
    }
}