package unipd.edids.logicBusiness.strategies.tenseStrategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PresentTenseStrategyTest {

    private static final Logger logger = LogManager.getLogger(PresentTenseStrategyTest.class);
    private PresentTenseStrategy presentTenseStrategy;
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: PresentTenseStrategyTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
        presentTenseStrategy = new PresentTenseStrategy();
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
        presentTenseStrategy = null;
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: PresentTenseStrategyTest");
    }

    @Test
    void testConjugateWithValidVerb() {
        logger.info("Testing conjugate() with a valid verb...");
        // Act
        String result = presentTenseStrategy.conjugate("run");

        // Assert
        assertEquals("run", result, "The method should return the input verb without any changes.");
    }

    @Test
    void testConjugateWithEmptyString() {
        logger.info("Testing conjugate() with an empty string...");
        // Act
        String result = presentTenseStrategy.conjugate("");

        // Assert
        assertEquals("", result, "The method should return the input string even if it is empty.");
    }

    @Test
    void testConjugateWithNullVerb() {
        logger.info("Testing conjugate() with null input...");
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> presentTenseStrategy.conjugate(null),
                "The method should throw an IllegalArgumentException when the input is null."
        );
        assertEquals("The verb cannot be null.", exception.getMessage(),
                "The exception message should indicate that the verb cannot be null.");
    }
}