package unipd.edids.logicBusiness.strategies.tenseStrategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FutureTenseStrategyTest {

    private static final Logger logger = LogManager.getLogger(FutureTenseStrategyTest.class);
    private FutureTenseStrategy futureTenseStrategy;
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: FutureTenseStrategyTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
        futureTenseStrategy = new FutureTenseStrategy();
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
        futureTenseStrategy = null;
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: FutureTenseStrategyTest");
    }

    @Test
    void testConjugateWithNullInput() {
        logger.info("Testing conjugate() with null input...");
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> futureTenseStrategy.conjugate(null),
                "Expected IllegalArgumentException when providing a null verb");
        assertEquals("The verb cannot be null.", exception.getMessage(),
                "Expected exception message to be 'The verb cannot be null.'");
    }

    @Test
    void testConjugateWithSpecialCaseVerbIs() {
        logger.info("Testing conjugate() with special case verb 'is'...");
        // Act
        String result = futureTenseStrategy.conjugate("is");

        // Assert
        assertEquals("will be", result, "Conjugation for 'is' is incorrect, expected 'will be'.");
    }

    @Test
    void testConjugateWithSpecialCaseVerbAm() {
        logger.info("Testing conjugate() with special case verb 'am'...");
        // Act
        String result = futureTenseStrategy.conjugate("am");

        // Assert
        assertEquals("will be", result, "Conjugation for 'am' is incorrect, expected 'will be'.");
    }



    @Test
    void testConjugateWithSpecialCaseVerbAre() {
        logger.info("Testing conjugate() with special case verb 'are'...");
        // Act
        String result = futureTenseStrategy.conjugate("are");

        // Assert
        assertEquals("will be", result, "Conjugation for 'are' is incorrect, expected 'will be'.");
    }
    @Test
    void testConjugateWithSpecialCaseVerbHas() {
        logger.info("Testing conjugate() with special case verb 'has'...");
        // Act
        String result = futureTenseStrategy.conjugate("has");

        // Assert
        assertEquals("will have", result, "Conjugation for 'has' is incorrect, expected 'will have'.");
    }

    @Test
    void testConjugateWithRegularVerb() {
        logger.info("Testing conjugate() with a regular verb...");
        // Act
        String result = futureTenseStrategy.conjugate("run");

        // Assert
        assertEquals("will run", result, "Conjugation for 'run' is incorrect, expected 'will run'.");
    }


    @Test
    void testConjugateWithEmptyString() {
        logger.info("Testing conjugate() with an empty string...");
        // Act
        String result = futureTenseStrategy.conjugate("");

        // Assert
        assertEquals("will ", result, "Conjugation for an empty string is incorrect, expected 'will '.");
    }

    @Test
    void testConjugateWithCapitalizedVerb() {
        logger.info("Testing conjugate() with a capitalized verb...");
        // Act
        String result = futureTenseStrategy.conjugate("Walk");

        // Assert
        assertEquals("will Walk", result, "Conjugation for 'Walk' is incorrect, expected 'will Walk'.");
    }
}