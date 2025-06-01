package unipd.edids.logicBusiness.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.exceptions.MissingApiKeyException;
import unipd.edids.logicBusiness.managers.ConfigManager;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class APIClientTest {

    private static final Logger logger = LogManager.getLogger(APIClientTest.class);
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        try {
            // Gets the API key; throws MissingApiKeyException if not configured.
            ConfigManager.getInstance().getProperty("api.key.file");
        } catch (MissingApiKeyException e) {
            logger.warn("Skipping test due to missing API Key: {}", e.getMessage());
            Assumptions.assumeTrue(false, "Test skipped: API Key is not configured.");
        }
        logger.info("Starting test suite: APIClientTest");
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
        logger.info("Finished test suite: APIClientTest");
    }

    @Test
    void testSetAPITypeWithModeration() {
        logger.info("Testing setAPIType with MODERATION...");
        // Arrange
        APIClient<Object> apiClient = new APIClient<>();

        // Act
        APIClient<Object> returnedClient = apiClient.setAPIType(APIClient.RequestType.MODERATION);

        // Assert
        assertNotNull(returnedClient, "Returned client should not be null after setting API type to MODERATION.");
        assertEquals(APIClient.RequestType.MODERATION, apiClient.getRequestType(), "API type should be set to MODERATION.");
    }

    @Test
    void testSetAPITypeWithSyntax() {
        logger.info("Testing setAPIType with SYNTAX...");
        // Arrange
        APIClient<Object> apiClient = new APIClient<>();

        // Act
        APIClient<Object> returnedClient = apiClient.setAPIType(APIClient.RequestType.SYNTAX);

        // Assert
        assertNotNull(returnedClient, "Returned client should not be null after setting API type to SYNTAX.");
        assertEquals(APIClient.RequestType.SYNTAX, apiClient.getRequestType(), "API type should be set to SYNTAX.");
    }

    @Test
    void testServiceUnavailable() {
        logger.info("Testing isServiceAvailable with simulated service unavailability...");
        // Arrange
        APIClient<Object> apiClient = new APIClient<Object>() {
            @Override
            public void isServiceAvailable() {
                throw new RuntimeException("Simulated service unavailability.");
            }
        };

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, apiClient::isServiceAvailable,
                "Expected a RuntimeException to be thrown, but it was not.");
        assertEquals("Simulated service unavailability.", exception.getMessage(),
                "Unexpected exception message when simulating service unavailability.");
    }
}