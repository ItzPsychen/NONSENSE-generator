package unipd.edids.logicBusiness.factories;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import unipd.edids.logicBusiness.entities.Adjective;
import unipd.edids.logicBusiness.entities.Noun;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.entities.Word;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WordFactoryTest {

    @BeforeAll
    static void setUp() throws IOException {
        // Create temporary mock files for each WordType
        File tempNounFile = File.createTempFile("noun", ".txt");
        File tempAdjectiveFile = File.createTempFile("adjective", ".txt");
        File tempVerbFile = File.createTempFile("verb", ".txt");

        tempNounFile.deleteOnExit();
        tempAdjectiveFile.deleteOnExit();
        tempVerbFile.deleteOnExit();

        // Populate files with mock data
        try (FileWriter nounWriter = new FileWriter(tempNounFile);
             FileWriter adjectiveWriter = new FileWriter(tempAdjectiveFile);
             FileWriter verbWriter = new FileWriter(tempVerbFile)) {
            nounWriter.write("apple\nbanana\n");
            adjectiveWriter.write("quick\nlazy\n");
            verbWriter.write("run\njump\n");
        }

        ConfigManager.getInstance().setProperty("noun.file", tempNounFile.getAbsolutePath());
        ConfigManager.getInstance().setProperty("adjective.file", tempAdjectiveFile.getAbsolutePath());
        ConfigManager.getInstance().setProperty("verb.file", tempVerbFile.getAbsolutePath());
        // Initialize Singleton Instances
        Noun.getInstance();
        Adjective.getInstance();
        Verb.getInstance();
    }

    @AfterAll
    static void tearDown() throws IOException {
        ConfigManager.getInstance().resetDefault(ConfigManager.getInstance().getProperty("api.key.file"));
    }

    @Test
    void testGetWordProviderReturnsNoun() {
        Word result = WordFactory.getWordProvider(WordFactory.WordType.NOUN);

        assertNotNull(result, "The returned Word for NOUN must not be null.");
        assertEquals(Noun.getInstance(), result,
                "The returned Word for NOUN must be the same instance as Noun.getInstance().");
    }

    @Test
    void testGetWordProviderReturnsAdjective() {
        Word result = WordFactory.getWordProvider(WordFactory.WordType.ADJECTIVE);

        assertNotNull(result, "The returned Word for ADJECTIVE must not be null.");
        assertEquals(Adjective.getInstance(), result,
                "The returned Word for ADJECTIVE must be the same instance as Adjective.getInstance().");
    }

    @Test
    void testGetWordProviderReturnsVerb() {
        Word result = WordFactory.getWordProvider(WordFactory.WordType.VERB);

        assertNotNull(result, "The returned Word for VERB must not be null.");
        assertEquals(Verb.getInstance(), result,
                "The returned Word for VERB must be the same instance as Verb.getInstance().");
    }
}