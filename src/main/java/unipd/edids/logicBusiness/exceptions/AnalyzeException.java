package unipd.edids.logicBusiness.exceptions;

/**
 * Custom runtime exception that encapsulates errors occurring during sentence analysis.
 *
 * <p>Responsibilities:
 * - Acts as a specific exception type for errors in the sentence analysis process.
 * - Provides detailed messages and causes for analysis-related issues.
 *
 * <p>Design Patterns:
 * - Follows the Exception Hierarchy Design pattern for custom exception handling.
 */
public class AnalyzeException extends RuntimeException {
    public AnalyzeException(String message, Throwable cause) {
        super(message, cause);
    }
}