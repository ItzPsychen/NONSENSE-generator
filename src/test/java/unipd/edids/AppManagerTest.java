package unipd.edids;

import org.junit.jupiter.api.Test;
import unipd.edids.logicBusiness.AppManager;
import unipd.edids.logicBusiness.entities.Sentence;

import static org.junit.jupiter.api.Assertions.*;

class AppManagerTest {

    @Test
    void testGenerateSentenceWithRandomStrategyAndNewWords() {
        AppManager appManager = new AppManager();
        appManager.analyzeSentence("The quick brown fox jumps over the lazy dog.", false);

        Sentence result = appManager.generateSentence("RANDOM", null, false, false, true, false);

        assertNotNull(result);
        assertTrue(result.getSentence().length() > 0);
    }

    @Test
    void testGenerateSentenceWithSameStrategyAndFutureTense() {
        AppManager appManager = new AppManager();
        appManager.analyzeSentence("She is reading a book.", false);

        Sentence result = appManager.generateSentence("SAME", null, false, true, false, false);

        assertNotNull(result);
        assertTrue(result.getSentence().length() > 0);
    }

    @Test
    void testGenerateSentenceWithSelectedStrategyAndToxicityModeration() {
        AppManager appManager = new AppManager();
        appManager.analyzeSentence("We should finish this project soon.", false);

        Sentence result = appManager.generateSentence("SELECTED", "SVO", true, false, false, false);

        assertNotNull(result);
        assertTrue(result.getSentence().length() > 0);
    }

    @Test
    void testGenerateSentenceThrowsExceptionWhenInputSentenceNullAndNewWordsDisabled() {
        AppManager appManager = new AppManager();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> appManager.generateSentence("SAME", null, false, false, false, false)
        );

        assertEquals(
                "Input sentence cannot be null. Please analyze a sentence first or enable the 'new words' option while selecting a valid structure (Random or Selected).",
                exception.getMessage()
        );
    }

    @Test
    void testGenerateSentenceWithInvalidStrategy() {
        AppManager appManager = new AppManager();
        appManager.analyzeSentence("This is an example sentence.", false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> appManager.generateSentence("INVALID", null, false, false, false, false)
        );

        assertEquals("Invalid strategy: INVALID", exception.getMessage());
    }

    @Test
    void testGenerateSentenceSavesToFileWhenSaveSelectedIsTrue() {
        AppManager appManager = new AppManager();
        appManager.analyzeSentence("The apple is red.", false);

        Sentence result = appManager.generateSentence("RANDOM", null, false, false, true, true);

        assertNotNull(result);
        assertTrue(result.getSentence().length() > 0);
        // Additional verification may involve mocking FileManager, if required, to confirm save behavior.
    }
}