package unipd.edids;

import com.google.cloud.language.v1.*;
import java.util.List;

public class ModerationSentenceService {

    /**
     * Analizza il livello di tossicità e modera il testo fornito.
     *
     * @return un oggetto `ModerateTextResponse` contenente i risultati della moderazione del testo.
     */
    public static List<ClassificationCategory> moderateText(Sentence sentence) {
        LanguageServiceClient language = APIClient.getInstance(); // Ottieni il client singleton

        System.out.println("Sentence: " + sentence.getSentence().toString());
        try {
            // Crea il documento da moderare
            Document doc = Document.newBuilder().setContent(sentence.getSentence().toString()).setType(Document.Type.PLAIN_TEXT).build();

            // Crea la richiesta di moderazione
            ModerateTextRequest request = ModerateTextRequest.newBuilder().setDocument(doc).build();


            // Recupera la risposta dalla ModerateText API
            ModerateTextResponse response = language.moderateText(request);

            // TODO scegliere categorie da mettere a posto
            // Toxic - Profanity - Insult - Threat - Identity Threat

            sentence.setToxicity(getModerationConfidenceByName(response.getModerationCategoriesList(), "Toxic"));
            sentence.setProfanity(getModerationConfidenceByName(response.getModerationCategoriesList(), "Profanity"));
            sentence.setInsult(getModerationConfidenceByName(response.getModerationCategoriesList(), "Insult"));
            // sentence.setThreat(getModerationConfidenceByName(response.getModerationCategoriesList(), "Threat"));
            sentence.setIdentityThreat(response.getModerationCategories(5).getConfidence());
            response.getModerationCategoriesList().forEach(category -> System.out.println("Category: " + category.getName() + ", Confidence: " + category.getConfidence()));

            return response.getModerationCategoriesList();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static float getModerationConfidenceByName(List<ClassificationCategory> categories, String categoryName) {
        if (categories == null || categoryName == null) {
            throw new IllegalArgumentException("categories and categoryName cannot be null");
        }

        // Cerca la categoria con il nome specificato
        for (ClassificationCategory category : categories) {
            if (categoryName.equals(category.getName())) {
                return category.getConfidence();
            }
        }
        throw new RuntimeException("Category with name " + categoryName + " not found in response");
    }
}