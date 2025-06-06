package unipd.edids.logicBusiness.exceptions;

/**
 * Custom exception to handle a missing API key.
 *
 * <p>This exception is thrown when the API key required
 * to interact with an external service is not configured or is invalid.</p>
 *
 * <p>Purpose:
 * - Clearly indicate missing critical API configurations.
 * - Provide useful diagnostics for the developer or user.</p>
 */
public class MissingApiKeyException extends RuntimeException {

    /**
     * Constructs a new exception with the default error message.
     */
    public MissingApiKeyException() {
        super("API Key is missing. Please configure the API Key in File > Settings.");
    }

    /**
     * Constructs a new exception with a custom error message.
     *
     * @param message The detailed error message.
     */
    public MissingApiKeyException(String message) {
        super(message);
    }

   }