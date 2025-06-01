package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.entities.Adjective;
import unipd.edids.logicBusiness.entities.Noun;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NewWordStrategyTest {

    private static final Logger logger = LogManager.getLogger(NewWordStrategyTest.class);
    private NewWordStrategy newWordStrategy;
    private int testNumber = 0;
    private File tempNounFile;
    private File tempAdjectiveFile;
    private File tempVerbFile;

    @BeforeAll
    void startTesting() {
        logger.info("Starting test suite: NewWordStrategyTest");
        try {
            tempNounFile = File.createTempFile("noun", ".txt");
            tempAdjectiveFile = File.createTempFile("adjective", ".txt");
            tempVerbFile = File.createTempFile("verb", ".txt");

            tempNounFile.deleteOnExit();
            tempAdjectiveFile.deleteOnExit();
            tempVerbFile.deleteOnExit();

            try(FileWriter nounWriter = new FileWriter(tempNounFile);
            FileWriter adjectiveWriter = new FileWriter(tempAdjectiveFile);
            FileWriter verbWriter = new FileWriter(tempVerbFile)){
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
    void setUp() {
        logger.info("Running test #{}", ++testNumber);
        newWordStrategy = new NewWordStrategy();
    }

    @AfterEach
    void tearDown() {
        logger.info("Finished test #{}", testNumber);
        newWordStrategy = null;
    }

    @AfterAll
    void cleanUp() throws IOException {
        logger.info("Finished test suite: NewWordStrategyTest");
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
    void testPopulateWordsPopulatesNouns() {
        logger.info("Testing populateWords() for noun placeholders...");
        // Arrange
        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This is a [noun] and another [noun]."));

        // Act
        newWordStrategy.populateWords(sentence);

        // Assert
        int expectedNounsCount = 2;
        List<String> nouns = sentence.getNouns();
        assertEquals(expectedNounsCount, nouns.size(), "Noun list does not contain the expected number of words.");
        assertTrue(nouns.stream().allMatch(n -> n != null && !n.isEmpty()), "Noun list contains invalid words.");
    }

    @Test
    void testPopulateWordsPopulatesVerbs() {
        logger.info("Testing populateWords() for verb placeholders...");
        // Arrange
        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This [verb] and that [verb]."));
        Verb.getInstance().configureVerbTense(false);

        // Act
        newWordStrategy.populateWords(sentence);

        // Assert
        int expectedVerbsCount = 2;
        List<String> verbs = sentence.getVerbs();
        assertEquals(expectedVerbsCount, verbs.size(), "Verb list does not contain the expected number of words.");
        assertTrue(verbs.stream().allMatch(v -> v != null && !v.isEmpty()), "Verb list contains invalid words.");
    }

    @Test
    void testPopulateWordsPopulatesAdjectives() {
        logger.info("Testing populateWords() for adjective placeholders...");
        // Arrange
        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("A [adjective] day and a [adjective] night."));

        // Act
        newWordStrategy.populateWords(sentence);

        // Assert
        int expectedAdjectivesCount = 2;
        List<String> adjectives = sentence.getAdjectives();
        assertEquals(expectedAdjectivesCount, adjectives.size(), "Adjective list does not contain the expected number of words.");
        assertTrue(adjectives.stream().allMatch(a -> a != null && !a.isEmpty()), "Adjective list contains invalid words.");
    }

    @Test
    void testPopulateWordsWithNoPlaceholders() {
        logger.info("Testing populateWords() with no placeholders...");
        // Arrange
        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This sentence has no placeholders."));

        // Act
        newWordStrategy.populateWords(sentence);

        // Assert
        assertTrue(sentence.getNouns().isEmpty(), "Noun list should be empty when no noun placeholders exist.");
        assertTrue(sentence.getVerbs().isEmpty(), "Verb list should be empty when no verb placeholders exist.");
        assertTrue(sentence.getAdjectives().isEmpty(), "Adjective list should be empty when no adjective placeholders exist.");
    }

    @Test
    void testPopulateWordsDoesNotDuplicateExistingWords() {
        logger.info("Testing populateWords() to avoid duplicating existing words...");
        // Arrange
        List<String> predefinedNouns = new ArrayList<>();
        predefinedNouns.add("cat");
        predefinedNouns.add("dog");

        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This is a [noun] and another [noun]."));
        sentence.setNouns(predefinedNouns);

        // Act
        newWordStrategy.populateWords(sentence);

        // Assert
        List<String> nouns = sentence.getNouns();
        assertEquals(predefinedNouns.size(), nouns.size(),
                "Noun list size should not change if enough words exist.");
        assertTrue(nouns.containsAll(predefinedNouns), "Existing nouns should not be modified.");
    }

    @Test
    void testPopulateWordsPartiallyPopulatesLists() {
        logger.info("Testing populateWords() for partial list population...");
        // Arrange
        List<String> predefinedNouns = new ArrayList<>();
        predefinedNouns.add("cat");

        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This is a [noun] and another [noun]."));
        sentence.setNouns(predefinedNouns);

        // Act
        newWordStrategy.populateWords(sentence);

        // Assert
        assertEquals(2, sentence.getNouns().size(),
                "Noun list should contain two words after partial population.");
    }
}