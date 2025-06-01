package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.entities.Adjective;
import unipd.edids.logicBusiness.entities.Noun;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.strategies.tenseStrategies.FutureTenseStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OriginalWordStrategyTest {

    private static final Logger logger = LogManager.getLogger(OriginalWordStrategyTest.class);
    private OriginalWordStrategy originalWordStrategy;
    private int testNumber = 0;
    private File tempNounFile;
    private File tempAdjectiveFile;
    private File tempVerbFile;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: OriginalWordStrategyTest");
        try {
            tempNounFile = File.createTempFile("noun", ".txt");
            tempAdjectiveFile = File.createTempFile("adjective", ".txt");
            tempVerbFile = File.createTempFile("verb", ".txt");

            tempNounFile.deleteOnExit();
            tempAdjectiveFile.deleteOnExit();
            tempVerbFile.deleteOnExit();

            try (FileWriter nounWriter = new FileWriter(tempNounFile);
                 FileWriter adjectiveWriter = new FileWriter(tempAdjectiveFile);
                 FileWriter verbWriter = new FileWriter(tempVerbFile)) {
                nounWriter.write("apple\nbanana\n");
                adjectiveWriter.write("quick\nlazy\n");
                verbWriter.write("run\njump\n");
            } catch (IOException e) {
                logger.error("Failed to reset temporary files for WordFactoryTest", e);
                fail("Failed to reset temporary files for WordFactoryTest");
            }

            ConfigManager.getInstance().setProperty("noun.file", tempNounFile.getAbsolutePath());
            ConfigManager.getInstance().setProperty("adjective.file", tempAdjectiveFile.getAbsolutePath());
            ConfigManager.getInstance().setProperty("verb.file", tempVerbFile.getAbsolutePath());
            Noun.getInstance();
            Adjective.getInstance();
            Verb.getInstance();

        } catch (IOException e) {
            logger.error("Failed to set up temporary files for WordFactoryTest", e);
            fail("Failed to reset temporary files for WordFactoryTest");
        }
    }

    @BeforeEach
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
        originalWordStrategy = null;
    }

    @AfterAll
    void cleanUp() throws IOException {
        logger.info("Finished test suite: OriginalWordStrategyTest");
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

    @Test
    void testPopulateWordsCopiesInputSentenceWords() {
        logger.info("Testing copy of input sentence words to temp sentence...");
        // Arrange
        Sentence inputSentence = new Sentence();
        inputSentence.setNouns(List.of("dog", "cat"));
        inputSentence.setVerbs(List.of("run", "jump"));
        inputSentence.setAdjectives(List.of("quick", "lazy"));

        Sentence tempSentence = new Sentence();
        originalWordStrategy = new OriginalWordStrategy(inputSentence);

        // Act
        originalWordStrategy.populateWords(tempSentence);

        // Assert
        assertTrue(inputSentence.getNouns().containsAll(tempSentence.getNouns()),
                "Nouns were not copied correctly.");
        assertTrue(inputSentence.getVerbs().containsAll(tempSentence.getVerbs()),
                "Verbs were not copied correctly.");
        assertTrue(inputSentence.getAdjectives().containsAll(tempSentence.getAdjectives()),
                "Adjectives were not copied correctly.");
    }

    @Test
    void testPopulateWordsFillsPlaceholdersInStructure() {
        logger.info("Testing placeholder filling in structure...");
        // Arrange
        Sentence inputSentence = new Sentence();
        inputSentence.setNouns(List.of());
        inputSentence.setVerbs(List.of());
        inputSentence.setAdjectives(List.of());

        Sentence tempSentence = new Sentence();
        tempSentence.setStructure(new StringBuilder("[noun] [verb] [noun] [adjective]"));

        originalWordStrategy = new OriginalWordStrategy(inputSentence);
        Verb.getInstance().configureVerbTense(false);

        // Act
        originalWordStrategy.populateWords(tempSentence);

        // Assert
        assertEquals(2, tempSentence.getNouns().size(),
                "Nouns placeholder(s) were not filled correctly.");
        assertEquals(1, tempSentence.getVerbs().size(),
                "Verb placeholder(s) were not filled correctly.");
        assertEquals(1, tempSentence.getAdjectives().size(),
                "Adjective placeholder(s) were not filled correctly.");
    }

    @Test
    void testPopulateWordsConjugatesFutureTenseVerbs() {
        logger.info("Testing future-tense verb conjugation...");
        // Arrange
        Sentence inputSentence = new Sentence();
        inputSentence.setVerbs(List.of("go", "come"));

        FutureTenseStrategy futureTenseStrategy = new FutureTenseStrategy();
        Verb.getInstance().configureVerbTense(true);

        Sentence tempSentence = new Sentence();
        originalWordStrategy = new OriginalWordStrategy(inputSentence);

        // Act
        originalWordStrategy.populateWords(tempSentence);

        // Assert
        tempSentence.getVerbs().forEach(verb ->
                assertNotNull(futureTenseStrategy.conjugate(verb), "Future tense conjugation failed."));
    }

    @Test
    void testPopulateWordsShufflesWordLists() {
        logger.info("Testing shuffle of word lists...");
        // Arrange
        Sentence inputSentence = new Sentence();
        inputSentence.setNouns(List.of("apple", "banana", "cherry"));
        inputSentence.setVerbs(List.of("eat", "drink", "consume"));
        inputSentence.setAdjectives(List.of("fresh", "ripe", "juicy"));

        Sentence tempSentence = new Sentence();
        originalWordStrategy = new OriginalWordStrategy(inputSentence);

        // Act
        originalWordStrategy.populateWords(tempSentence);

        // Assert
        assertEquals(3, tempSentence.getNouns().size(),
                "Noun list size should not change after shuffle.");
        assertEquals(3, tempSentence.getVerbs().size(),
                "Verb list size should not change after shuffle.");
        assertEquals(3, tempSentence.getAdjectives().size(),
                "Adjective list size should not change after shuffle.");
    }
}