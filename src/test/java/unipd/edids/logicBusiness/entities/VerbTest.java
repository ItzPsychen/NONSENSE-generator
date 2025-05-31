package unipd.edids.logicBusiness.entities;

import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.strategies.tenseStrategies.FutureTenseStrategy;
import unipd.edids.logicBusiness.strategies.tenseStrategies.PresentTenseStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VerbTest {

    private static File tempFile;

    @BeforeEach
    void setupClass() throws IOException {
        tempFile = File.createTempFile("verbs", ".txt");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("run\njump\nwalk");
        }
        ConfigManager.getInstance().setProperty("verb.file", tempFile.getAbsolutePath());
    }

    @AfterEach
    void cleanupClass() throws IOException {
        ConfigManager.getInstance().resetDefault(ConfigManager.getInstance().getProperty("api.key.file"));
    }

    @Test
    void testGetRandomWordReturnsAValidWord() {
        Verb verb = Verb.getInstance();
        verb.loadWords(tempFile.getAbsolutePath());

        for (int i = 0; i < 100; i++) { // Test multiple times for randomness
            String randomWord = verb.getRandomWord();
            assertTrue(List.of("run", "jump", "walk").contains(randomWord),
                    "getRandomWord() should return a word from the file but returned: " + randomWord);
        }

    }

    @Test
    void testGetRandomWordReturnsUndefinedWhenNoWordsAvailable() throws IOException {
        // Clear all words intentionally
        ConfigManager.getInstance().setProperty("verb.file", File.createTempFile("verbs", ".txt").getAbsolutePath());


        Verb verb = Verb.getInstance();
        String randomWord = verb.getRandomWord();

        assertEquals("undefined", randomWord, "Random word should return 'undefined' when no words are available");
    }

    @Test
    void testConjugateUsesPresentTenseByDefault() {
        Verb verb = Verb.getInstance();
        verb.configureVerbTense(false); // Ensure present tense is default
        String conjugatedWord = verb.conjugate("run");

        assertNotNull(conjugatedWord, "Conjugated word should not be null in present tense");
    }

    @Test
    void testConjugateSwitchesToFutureTense() {
        Verb verb = Verb.getInstance();
        verb.configureVerbTense(true); // Switch to future tense
        String conjugatedWord = verb.conjugate("jump");

        assertNotNull(conjugatedWord, "Conjugated word should not be null in future tense");
    }

    @Test
    void testTenseStrategyChangesBasedOnConfiguration() {
        Verb verb = Verb.getInstance();
        verb.configureVerbTense(false);
        assertInstanceOf(PresentTenseStrategy.class, verb.getTenseStrategy(), "Tense strategy should be PresentTenseStrategy when futureTense is false");

        verb.configureVerbTense(true);
        assertInstanceOf(FutureTenseStrategy.class, verb.getTenseStrategy(), "Tense strategy should be FutureTenseStrategy when futureTense is true");
    }
}