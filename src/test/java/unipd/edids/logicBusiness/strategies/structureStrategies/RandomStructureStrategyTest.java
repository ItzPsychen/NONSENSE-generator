package unipd.edids.logicBusiness.strategies.structureStrategies;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomStructureStrategyTest {

    private RandomStructureStrategy randomStructureStrategy;

    @BeforeEach
    void setUp() {
        randomStructureStrategy = new RandomStructureStrategy();
    }

    @AfterEach
    void tearDown() {
        randomStructureStrategy = null;
    }

    /**
     * Test to ensure that generateSentenceStructure() returns a non-null structure.
     */
    @Test
    void testGenerateSentenceStructureNonNull() {
        StringBuilder result = randomStructureStrategy.generateSentenceStructure();
        assertNotNull(result, "The generated sentence structure should not be null.");
    }

    /**
     * Test to ensure that generateSentenceStructure() returns a StringBuilder containing a valid random structure.
     */
    @Test
    void testGenerateSentenceStructureNotEmpty() {
        StringBuilder result = randomStructureStrategy.generateSentenceStructure();
        assertTrue(result.length() > 0, "The generated sentence structure should not be empty.");
    }

    /**
     * Test to ensure that multiple calls to generateSentenceStructure() produce potentially different results,
     * validating randomness.
     */
    @Test
    void testGenerateSentenceStructureRandomness() {
        StringBuilder firstResult = randomStructureStrategy.generateSentenceStructure();
        StringBuilder secondResult = randomStructureStrategy.generateSentenceStructure();
        assertNotEquals(firstResult.toString(), secondResult.toString(),
                "The generated sentence structures should be potentially different, ensuring randomness.");
    }
}