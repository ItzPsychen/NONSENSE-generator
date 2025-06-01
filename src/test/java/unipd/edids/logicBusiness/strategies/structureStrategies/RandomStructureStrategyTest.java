package unipd.edids.logicBusiness.strategies.structureStrategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RandomStructureStrategyTest {

    private static final Logger logger = LogManager.getLogger(RandomStructureStrategyTest.class);
    private int testNumber = 0;
    private RandomStructureStrategy randomStructureStrategy;
    private static final String TEMP_FILE_PATH = "temporary_sentence_structures.properties";
    private File tempFile;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: RandomStructureStrategyTest");
        try {
            // Creazione di un file temporaneo
            tempFile = new File(TEMP_FILE_PATH);
            if (tempFile.createNewFile()) {
                logger.info("Temporary file created: {}", TEMP_FILE_PATH);
                // Impostazione della proprietà sentence.structures
            } else {
                throw new IOException("Failed to create temporary file.");
            }
        } catch (IOException e) {
            logger.error("Error during setup of test suite: {}", e.getMessage());
            fail("Failed to set up before all tests.");
        }
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
        randomStructureStrategy = new RandomStructureStrategy();
        try (FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write("[noun] [verb] [noun]\n[noun] [verb] the [noun]\n[noun] [verb] the [adjective] [noun]");
        } catch (IOException e) {
            logger.error("Failed to write to temporary file for AdjectiveTest", e);
            fail("Failed to write to temporary file for AdjectiveTest");
        }
        ConfigManager.getInstance().setProperty("sentence.structures", tempFile.getAbsolutePath());

    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
        randomStructureStrategy = null;
    }

    @AfterAll
    void cleanUp() {
        logger.info("Finished test suite: RandomStructureStrategyTest");
        // Eliminazione del file temporaneo e reset di ConfigManager
        try {
            if (tempFile != null && tempFile.exists()) {
                if (tempFile.delete()) {
                    logger.info("Temporary file deleted: {}", TEMP_FILE_PATH);
                } else {
                    throw new IOException("Failed to delete temporary file: " + TEMP_FILE_PATH);
                }
            }
            ConfigManager.getInstance().resetDefault();
            logger.info("ConfigManager reset to default.");
        } catch (IOException e) {
            logger.error("Error during cleanup after all tests: {}", e.getMessage());
        }
    }

    @Test
    void testGenerateSentenceStructureNonNull() {
        logger.info("Testing generateSentenceStructure() to ensure it returns a non-null structure...");
        // Act
        StringBuilder result = randomStructureStrategy.generateSentenceStructure();

        // Assert
        assertNotNull(result, "The generated sentence structure should not be null.");
        assertTrue(List.of("[noun] [verb] [noun]", "[noun] [verb] the [noun]", "[noun] [verb] the [adjective] [noun]").contains(result.toString()), "The generated sentence structure should be one of the expected values.");
    }

    @Test
    void testGenerateSentenceStructureNotEmpty() {
        logger.info("Testing generateSentenceStructure() to ensure it returns a non-empty structure...");
        // Act
        StringBuilder result = randomStructureStrategy.generateSentenceStructure();

        // Assert
        assertFalse(result.isEmpty(), "The generated sentence structure should not be empty.");
    }

    @Test
    void testGenerateSentenceStructureRandomness() {
        logger.info("Testing generateSentenceStructure() to ensure randomness across results...");
        // Act
        int numIterations = 1000;
        boolean differentValuesFound = false;
        StringBuilder firstResult = randomStructureStrategy.generateSentenceStructure();

        for(int i = 0; i < numIterations && !differentValuesFound; i++) {
            StringBuilder secondResult = randomStructureStrategy.generateSentenceStructure();
            if(!firstResult.toString().contentEquals(secondResult)) {
                differentValuesFound = true;
            }
        }

        // Assert
        assertTrue(differentValuesFound,
                "The generated sentence structures should be different after multiple attempts.");
    }
}