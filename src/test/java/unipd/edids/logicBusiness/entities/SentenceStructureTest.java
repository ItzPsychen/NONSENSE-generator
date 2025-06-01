package unipd.edids.logicBusiness.entities;

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
public class SentenceStructureTest {

    private static final Logger logger = LogManager.getLogger(SentenceStructureTest.class);
    private File tempFile;
    private int testNumber = 0;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: SentenceStructureTest");
        try {
            tempFile = File.createTempFile("sentence_structures", ".txt");
            tempFile.deleteOnExit();


        } catch (IOException e) {
            logger.error("Failed to create temporary file for SentenceStructureTest", e);
            fail("Failed to write to temporary file for SentenceStructureTest");
        }
    }

    @BeforeEach
    void createTempFile() {
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("[NOUN] [VERB]\n[ADJECTIVE] [NOUN]\n[VERB] [NOUN] [ADJECTIVE]\n");
        } catch (IOException e) {
            logger.error("Failed to write to temporary file for SentenceStructureTest", e);
            fail("Failed to write to temporary file for SentenceStructureTest");
        }
        ConfigManager.getInstance().setProperty("sentence.structures", tempFile.getAbsolutePath());
        testNumber++;
        logger.info("Starting test #{}", testNumber);
    }

    @Test
    void testSingletonInstance() {
        SentenceStructure instance1 = SentenceStructure.getInstance();
        SentenceStructure instance2 = SentenceStructure.getInstance();

        assertNotNull(instance1, "The instance should not be null.");
        assertSame(instance1, instance2, "The two instances should be identical (singleton).");
    }

    @Test
    void testRandomStructureRetrieval() {
        SentenceStructure instance = SentenceStructure.getInstance();
        String randomStructure = instance.getRandomStructure();
        List<String> expectedStructures = List.of(
                "[NOUN] [VERB]",
                "[ADJECTIVE] [NOUN]",
                "[VERB] [NOUN] [ADJECTIVE]"
        );

        assertNotNull(randomStructure, "The random structure should not be null.");
        assertTrue(expectedStructures.contains(randomStructure),
                "The random structure should be one of the loaded structures.");
    }

    @Test
    void testDefaultStructureWhenNoFile() throws IOException {
        File emptyTempFile = File.createTempFile("empty_structures", ".txt");
        emptyTempFile.deleteOnExit();

        ConfigManager.getInstance().setProperty("sentence.structures", emptyTempFile.getAbsolutePath());
        SentenceStructure.getInstance().onConfigChange("sentence.structures", emptyTempFile.getAbsolutePath());

        String randomStructure = SentenceStructure.getInstance().getRandomStructure();
        assertEquals("[NOUN] [VERB] [NOUN]", randomStructure,
                "Should return the default structure when no structures are available.");

        assertTrue(emptyTempFile.delete(), "The empty file was not deleted correctly.");
    }

    @Test
    void testOnConfigChangeReloadsStructures() throws IOException {
        File newTempFile = File.createTempFile("new_sentence_structures", ".txt");
        newTempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(newTempFile)) {
            writer.write("[ADJECTIVE] [ADJECTIVE] [NOUN]\n");
            writer.write("[VERB] [NOUN]\n");
        }
        ConfigManager.getInstance().setProperty("sentence.structures", newTempFile.getAbsolutePath());

        SentenceStructure instance = SentenceStructure.getInstance();
        instance.onConfigChange("sentence.structures", newTempFile.getAbsolutePath());

        List<String> newStructures = instance.getStructures();
        assertNotNull(newStructures, "The updated structures should not be null.");
        assertEquals(2, newStructures.size(), "Two new structures should be loaded.");
        assertTrue(newStructures.contains("[ADJECTIVE] [ADJECTIVE] [NOUN]"),
                "The new structures should include the updated one.");
        assertTrue(newStructures.contains("[VERB] [NOUN]"),
                "The new structures should include the updated one.");
    }

    @Test
    void testGetStructuresReturnsLoadedStructures() {
        SentenceStructure instance = SentenceStructure.getInstance();

        List<String> structures = instance.getStructures();
        assertNotNull(structures, "The list of structures should not be null.");
        assertFalse(structures.isEmpty(), "The list of structures should not be empty.");
        assertTrue(structures.contains("[NOUN] [VERB]"),
                "The structures should include all those defined in the file.");
    }

    @AfterEach
    void tearDown()  {
        logger.info("Finished test #{}", testNumber);
    }

    @AfterAll
    void cleanUp() throws IOException {
        logger.info("Finished test suite: SentenceStructureTest");
        ConfigManager.getInstance().resetDefault();

        if (tempFile.exists()) {
            assertTrue(tempFile.delete(), "Failed to delete the temporary file.");
        }
    }
}