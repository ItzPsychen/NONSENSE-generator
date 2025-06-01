package unipd.edids.logicBusiness.strategies.structureStrategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StructureSentenceStrategyTest {

    private static final Logger logger = LogManager.getLogger(StructureSentenceStrategyTest.class);
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: StructureSentenceStrategyTest");
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
        logger.info("Finished test suite: StructureSentenceStrategyTest");
    }

    @Test
    void testGenerateSentenceStructureForDefaultImplementation() {
        logger.info("Testing generateSentenceStructure() for a default implementation...");
        // Arrange
        StructureSentenceStrategy strategy = new StructureSentenceStrategy() {
            @Override
            public StringBuilder generateSentenceStructure() {
                return new StringBuilder("Default Sentence Structure");
            }
        };

        // Act
        StringBuilder result = strategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "Resulting StringBuilder should not be null");
        assertEquals("Default Sentence Structure", result.toString(),
                "The returned sentence structure does not match the expected value");
    }

    @Test
    void testGenerateSentenceStructureForEmptyStructure() {
        logger.info("Testing generateSentenceStructure() for an empty structure...");
        // Arrange
        StructureSentenceStrategy emptyStrategy = new StructureSentenceStrategy() {
            @Override
            public StringBuilder generateSentenceStructure() {
                return new StringBuilder();
            }
        };

        // Act
        StringBuilder result = emptyStrategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "Resulting StringBuilder should not be null even for an empty structure");
        assertEquals("", result.toString(),
                "The returned sentence structure should be an empty string");
    }

    @Test
    void testGenerateSentenceStructureForCustomStructure() {
        logger.info("Testing generateSentenceStructure() for a custom structure...");
        // Arrange
        StructureSentenceStrategy customStrategy = new StructureSentenceStrategy() {
            @Override
            public StringBuilder generateSentenceStructure() {
                return new StringBuilder("This is a custom structure.");
            }
        };

        // Act
        StringBuilder result = customStrategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "Resulting StringBuilder should not be null");
        assertEquals("This is a custom structure.", result.toString(),
                "The returned sentence structure does not match the expected custom value");
    }
}