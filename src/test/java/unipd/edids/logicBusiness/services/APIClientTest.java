package unipd.edids.logicBusiness.services;

import com.google.cloud.language.v1.AnalyzeSyntaxRequest;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.LanguageServiceClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class APIClientTest {

    @Test
    void testSetAPITypeWithModeration() {
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