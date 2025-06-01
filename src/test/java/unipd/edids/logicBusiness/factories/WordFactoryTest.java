package unipd.edids.logicBusiness.factories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.entities.Adjective;
import unipd.edids.logicBusiness.entities.Noun;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.entities.Word;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WordFactoryTest {

    private static final Logger logger = LogManager.getLogger(WordFactoryTest.class);
    private File tempNounFile;
    private File tempAdjectiveFile;
    private File tempVerbFile;
    private int testNumber = 0;

    @BeforeAll
    void setUp() {
        logger.info("Starting test suite: WordFactoryTest");
        try {
            tempNounFile = File.createTempFile("noun", ".txt");
            tempAdjectiveFile = File.createTempFile("adjective", ".txt");
            tempVerbFile = File.createTempFile("verb", ".txt");

            tempNounFile.deleteOnExit();
            tempAdjectiveFile.deleteOnExit();
            tempVerbFile.deleteOnExit();



            // Inizializza le istanze singleton
            Noun.getInstance();
            Adjective.getInstance();
            Verb.getInstance();

        } catch (IOException e) {
            logger.error("Failed to set up temporary files for WordFactoryTest", e);
            fail("Failed to reset temporary files for WordFactoryTest");
        }
    }

    @BeforeEach
    void resetConfig() {
        try (FileWriter nounWriter = new FileWriter(tempNounFile);
             FileWriter adjectiveWriter = new FileWriter(tempAdjectiveFile);
             FileWriter verbWriter = new FileWriter(tempVerbFile)) {
            nounWriter.write("apple\nbanana\n");
            adjectiveWriter.write("quick\nlazy\n");
            verbWriter.write("run\njump\n");
        } catch (IOException e){
            logger.error("Failed to reset temporary files for WordFactoryTest", e);
            fail("Failed to reset temporary files for WordFactoryTest");
        }
        ConfigManager.getInstance().setProperty("noun.file", tempNounFile.getAbsolutePath());
        ConfigManager.getInstance().setProperty("adjective.file", tempAdjectiveFile.getAbsolutePath());
        ConfigManager.getInstance().setProperty("verb.file", tempVerbFile.getAbsolutePath());

        testNumber++;
        logger.info("Starting test #{}", testNumber);
    }

    @Test
    void testGetWordProviderReturnsNoun() {
        logger.info("Running test: testGetWordProviderReturnsNoun");
        Word result = WordFactory.getWordProvider(WordFactory.WordType.NOUN);

        assertNotNull(result, "The returned Word for NOUN must not be null.");
        assertEquals(Noun.getInstance(), result,
                "The returned Word for NOUN must be the same instance as Noun.getInstance().");
    }

    @Test
    void testGetWordProviderReturnsAdjective() {
        logger.info("Running test: testGetWordProviderReturnsAdjective");
        Word result = WordFactory.getWordProvider(WordFactory.WordType.ADJECTIVE);

        assertNotNull(result, "The returned Word for ADJECTIVE must not be null.");
        assertEquals(Adjective.getInstance(), result,
                "The returned Word for ADJECTIVE must be the same instance as Adjective.getInstance().");
    }

    @Test
    void testGetWordProviderReturnsVerb() {
        logger.info("Running test: testGetWordProviderReturnsVerb");
        Word result = WordFactory.getWordProvider(WordFactory.WordType.VERB);

        assertNotNull(result, "The returned Word for VERB must not be null.");
        assertEquals(Verb.getInstance(), result,
                "The returned Word for VERB must be the same instance as Verb.getInstance().");
    }
    @AfterEach
    void tearDown() throws IOException {
        logger.info("Finished test #{}", testNumber);
    }



    @AfterAll
    void cleanUp() throws IOException {
        logger.info("Finished test suite: WordFactoryTest");
        ConfigManager.getInstance().resetDefault();

        if (tempNounFile != null && tempNounFile.exists()) {
            assertTrue(tempNounFile.delete(), "Failed to delete tempNounFile.");
        }
        if (tempAdjectiveFile != null && tempAdjectiveFile.exists()) {
            assertTrue(tempAdjectiveFile.delete(), "Failed to delete tempAdjectiveFile.");
        }
        if (tempVerbFile != null && tempVerbFile.exists()) {
            assertTrue(tempVerbFile.delete(), "Failed to delete tempVerbFile.");
        }
    }
}