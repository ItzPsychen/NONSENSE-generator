package unipd.edids.logicBusiness.services;

import com.google.cloud.language.v1.ClassificationCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unipd.edids.logicBusiness.entities.Sentence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ModerationSentenceService.
 * <p>
 * The class validates the behavior of the `moderateText` method and its associated private methods
 * to ensure proper handling of moderation logic and appropriate exceptions when necessary.
 */
class ModerationSentenceServiceTest {

    @Test
    void testGetModerationConfidenceByName_FoundCategory() {
        List<ClassificationCategory> categories = new ArrayList<>();
        categories.add(createCategory("Toxic", 0.9f));
        categories.add(createCategory("Profanity", 0.6f));
        float confidence = 0;
        try {
            confidence = invokeGetModerationConfidenceByName(categories, "Toxic");
        }
        catch (Exception e) {
            fail("Exception should not be thrown");
        }
        assertEquals(0.9f, confidence, 0.0001);
    }


    @Test
    void testSetCategoryConfidences_Success() {
        List<ClassificationCategory> categories = new ArrayList<>();
        categories.add(createCategory("Toxic", 0.8f));
        categories.add(createCategory("Profanity", 0.7f));
        categories.add(createCategory("Insult", 0.6f));
        categories.add(createCategory("Sexual", 0.5f));
        categories.add(createCategory("Politics", 0.4f));

        Sentence sentence = new Sentence();
        try {
            invokeSetCategoryConfidences(categories, sentence);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }

        assertEquals(0.8f, sentence.getToxicity(), 0.0001);
        assertEquals(0.7f, sentence.getProfanity(), 0.0001);
        assertEquals(0.6f, sentence.getInsult(), 0.0001);
        assertEquals(0.5f, sentence.getSexual(), 0.0001);
        assertEquals(0.4f, sentence.getPolitics(), 0.0001);
    }




    // --- METODI UTILI ---

    private ClassificationCategory createCategory(String name, float confidence) {
        return ClassificationCategory.newBuilder().setName(name).setConfidence(confidence).build();
    }

    private float invokeGetModerationConfidenceByName(List<ClassificationCategory> categories, String name) throws Exception {

            var method = ModerationSentenceService.class.getDeclaredMethod("getModerationConfidenceByName",
                    java.util.List.class, String.class
            );
            method.setAccessible(true);
            return (float) method.invoke(null, categories, name);

    }

    private void invokeSetCategoryConfidences(List<ClassificationCategory> categories, Sentence sentence) throws Exception {

            var method = ModerationSentenceService.class.getDeclaredMethod("setCategoryConfidences", List.class, Sentence.class);
            method.setAccessible(true);
            method.invoke(null, categories, sentence);

    }


    private Sentence validSentence;

    @BeforeEach
    void setup() {
        validSentence = new Sentence("This is a test sentence.");
    }

    @Test
    void testModerateText_Success() {
        ModerationSentenceService service = new ModerationSentenceService();

        // Temporary file for mocking response
        File tempFile;
        try {
            tempFile = File.createTempFile("moderateTextTest", ".tmp");
            tempFile.deleteOnExit();

            // Validate Execution
            assertDoesNotThrow(() -> service.moderateText(validSentence),
                    "moderateText should execute without exceptions for a valid sentence");

            // Validate sentence category values
            assertTrue(validSentence.getToxicity() >= 0, "Toxicity value must be set correctly");
            assertTrue(validSentence.getProfanity() >= 0, "Profanity value must be set correctly");
            assertTrue(validSentence.getInsult() >= 0, "Insult value must be set correctly");
            assertTrue(validSentence.getSexual() >= 0, "Sexual value must be set correctly");
            assertTrue(validSentence.getPolitics() >= 0, "Politics value must be set correctly");
        } catch (IOException e) {
            fail("Failed to create temporary file for test: " + e.getMessage());
        }
    }

    @Test
    void testModerateText_NullSentence_Failure() {
        ModerationSentenceService service = new ModerationSentenceService();

        Exception exception = assertThrows(RuntimeException.class,
                () -> service.moderateText(null),
                "moderateText should throw a RuntimeException when a null Sentence is passed");

        assertTrue(exception.getMessage().contains("Error while moderating text"),
                "Exception message should indicate error in text moderation");
    }

    @Test
    void testModerateText_EmptySentence_Failure() {
        ModerationSentenceService service = new ModerationSentenceService();

        Sentence emptySentence = new Sentence("");
        Exception exception = assertThrows(RuntimeException.class,
                () -> service.moderateText(emptySentence),
                "moderateText should throw a RuntimeException when an empty Sentence is passed");

        assertTrue(exception.getMessage().contains("Error while moderating text"),
                "Exception message should indicate error in text moderation");
    }

    @Test
    void testModerateText_InvalidCharactersAtStart_Failure() {
        ModerationSentenceService service = new ModerationSentenceService();
        Sentence invalidStartSentence = new Sentence("@Invalid sentence");

        Exception exception = assertThrows(RuntimeException.class,
                () -> service.moderateText(invalidStartSentence),
                "moderateText should throw a RuntimeException for text with invalid characters at the start");

        assertTrue(exception.getMessage().contains("Error while moderating text"),
                "Exception message should indicate error in text moderation");
    }

    @Test
    void testModerateText_InvalidCharactersAtEnd_Failure() {
        ModerationSentenceService service = new ModerationSentenceService();
        Sentence invalidEndSentence = new Sentence("Invalid sentence@");

        Exception exception = assertThrows(RuntimeException.class,
                () -> service.moderateText(invalidEndSentence),
                "moderateText should throw a RuntimeException for text with invalid characters at the end");

        assertTrue(exception.getMessage().contains("Error while moderating text"),
                "Exception message should indicate error in text moderation");
    }

    @Test
    void testModerateText_NoAlphabeticalCharacters_Failure() {
        ModerationSentenceService service = new ModerationSentenceService();
        Sentence noAlphaSentence = new Sentence("1234 5678");

        Exception exception = assertThrows(RuntimeException.class,
                () -> service.moderateText(noAlphaSentence),
                "moderateText should throw a RuntimeException for text with no alphabetical characters");

        assertTrue(exception.getMessage().contains("Error while moderating text"),
                "Exception message should indicate error in text moderation");
    }

    @Test
    void testModerateText_InvalidInternalCharacters_Failure() {
        ModerationSentenceService service = new ModerationSentenceService();
        Sentence invalidInternalChars = new Sentence("ValidText #Invalid&Char!");

        Exception exception = assertThrows(RuntimeException.class,
                () -> service.moderateText(invalidInternalChars),
                "moderateText should throw a RuntimeException for text with invalid internal characters");

        assertTrue(exception.getMessage().contains("Error while moderating text"),
                "Exception message should indicate error in text moderation");
    }

    @Test
    void testModerateText_ValidSentence_Success() {
        ModerationSentenceService service = new ModerationSentenceService();
        Sentence validSentence = new Sentence("This is a valid test sentence.");

        assertDoesNotThrow(() -> service.moderateText(validSentence),
                "moderateText should not throw an exception for valid sentences");
    }

    @AfterEach
    void cleanup() {
        validSentence = null;
    }
}

