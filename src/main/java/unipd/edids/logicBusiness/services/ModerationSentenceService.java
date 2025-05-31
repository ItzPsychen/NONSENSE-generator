package unipd.edids.logicBusiness.services;

import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.ModerateTextResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;

import java.util.List;


/**
 * Service class responsible for analyzing the toxicity levels of text content and moderating it.
 * <p>
 * Responsibilities:
 * - Validates input text to ensure it conforms to acceptable standards.
 * - Interacts with an external moderation API to determine categories of moderation confidence.
 * - Updates the `Sentence` object with toxicity levels based on moderation results.
 * - Logs detailed information during moderation for tracking and debugging.
 * <p>
 * Design Pattern:
 * - Utilizes the Utility pattern for methods like `getModerationConfidenceByName` to centralize reusable functionality.
 */
public class ModerationSentenceService {
    /**
     * Handles sentence moderation and categorization services.
     *
     * <p>Responsibilities:
     * - Facilitate text moderation processes.
     * - Interact with categorization and confidence assessment methods.
     *
     * <p>Design Pattern:
     * - Singleton pattern for logger management.
     */
    private static final Logger logger = LogManager.getLogger(ModerationSentenceService.class);

    /**
     * Retrieves the confidence value for a specific moderation category by its name from a list of classification categories.
     *
     * @param categories List of classification categories to search within.
     * @param categoryName The name of the category for which confidence is requested.
     * @return The confidence value of the specified category.
     * @throws IllegalArgumentException if categories or categoryName is null,
     *         or if the specified categoryName is not found in the list.
     */
    private static float getModerationConfidenceByName(List<ClassificationCategory> categories, String categoryName) {
        if (categories == null || categoryName == null) {
            String errorMessage = "Categories list or categoryName cannot be null";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        for (ClassificationCategory category : categories) {
            if (categoryName.equals(category.getName())) {
                logger.info("Found category: {} with confidence: {}", categoryName, category.getConfidence());
                return category.getConfidence();
            }
        }
        String errorMessage = "Category with name " + categoryName + " not found in response";
        logger.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * Moderates the content of the input Sentence by analyzing text for classification categories
     * and updating its confidence levels.
     *
     * @param sentence The Sentence object containing the text to be moderated and where
     *                 confidence levels will be updated.
     */
    public void moderateText(Sentence sentence) {
        try {
            validateText(sentence.getSentence().toString());
            logger.info("Starting text moderation for sentence: {}", sentence.getSentence());

            // Esegui la richiesta di moderazione
            ModerateTextResponse response = fetchModerationResponse(sentence);
            logger.info("Received moderation response");

            // Estrai la lista delle categorie
            List<ClassificationCategory> categories = response.getModerationCategoriesList();
            logger.info("Extracted {} moderation categories from the response", categories.size());

            // Imposta i livelli di confidenza delle categorie nel Sentence
            setCategoryConfidences(categories, sentence);
            logger.info("Set confidence levels on Sentence object");

            // Stampa informazioni sulle categorie con stream
            categories.forEach(category ->
                    logger.info("Category: {}, Confidence: {}", category.getName(), category.getConfidence())
            );

        } catch (Exception e) {
            String errorMessage = "Error while moderating text: " + e.getMessage();
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Fetches a moderation response for a given sentence.
     *
     * @param sentence the sentence to be moderated.
     * @return a {@code ModerateTextResponse} containing the moderation results for the given sentence.
     */
    private static ModerateTextResponse fetchModerationResponse(Sentence sentence) {
        logger.info("Fetching moderation response for sentence");
        return new APIClient<ModerateTextResponse>()
                .setAPIType(APIClient.RequestType.MODERATION)
                .setSentenceToAPI(sentence.getSentence().toString())
                .execute();
    }

    /**
     * Updates the confidence values for various categories in a given Sentence object
     * based on the provided classifications.
     *
     * @param categories a list of {@code ClassificationCategory} containing category names and their confidence values
     * @param sentence   the {@code Sentence} object whose category confidence values need to be set
     */
    private static void setCategoryConfidences(List<ClassificationCategory> categories, Sentence sentence) {
        logger.info("Setting category confidences for Sentence object");
        sentence.setToxicity(getModerationConfidenceByName(categories, "Toxic"));
        sentence.setProfanity(getModerationConfidenceByName(categories, "Profanity"));
        sentence.setInsult(getModerationConfidenceByName(categories, "Insult"));
        sentence.setSexual(getModerationConfidenceByName(categories, "Sexual"));
        sentence.setPolitics(getModerationConfidenceByName(categories, "Politics"));
    }


    /**
     * Validates the provided text based on predefined conditions such as presence of alphabetical characters,
     * absence of invalid characters, and proper formatting.
     *
     * @param sentenceText The text string to be validated. Cannot be null, empty, or contain invalid character patterns.
     */
    private void validateText(String sentenceText) {
        logger.debug("Validating text: {}", sentenceText);

        if (sentenceText == null || sentenceText.isEmpty()) {
            String errorMessage = "Input text cannot be null or empty.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (sentenceText.trim().isEmpty()) {
            String errorMessage = "Input text cannot be empty or whitespace only.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (!sentenceText.matches(".*[a-zA-Z]+.*")) {
            String errorMessage = "Input text must contain at least one alphabetical character.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (sentenceText.matches("^[^a-zA-Z0-9\\s].*")) {
            String errorMessage = "Input text contains invalid characters at the start of the text.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (sentenceText.matches(".*[^a-zA-Z0-9.\\s]$")) {
            String errorMessage = "Input text contains invalid characters at the end of the text.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (sentenceText.matches(".*[^a-zA-Z0-9.,:'\"\\s].*")) {
            String errorMessage = "Input text contains invalid characters.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

    }
}