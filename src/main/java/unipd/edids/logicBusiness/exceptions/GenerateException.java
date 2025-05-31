package unipd.edids.logicBusiness.exceptions;

/**
 * Custom runtime exception that encapsulates errors occurring during sentence generation.
 *
 * <p>Responsibilities:
 * - Acts as a specific exception for errors encountered in the sentence generation process.
 * - Provides descriptive messages and causes for generation-related issues.
 *
 * <p>Design Patterns:
 * - Follows the Exception Hierarchy Design pattern for custom exception handling.
 */
public class GenerateException extends RuntimeException {
    public GenerateException(String message, Throwable cause) {
        super(message, cause);
    }
}