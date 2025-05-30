package unipd.edids.logicBusiness.services;

import com.google.cloud.language.v1.*;
import unipd.edids.logicBusiness.entities.Sentence;

import java.util.List;

//fix forse serve un adapter? chiamare API bene

public class ModerationSentenceService {

    /**
     * Analizza il livello di tossicità e modera il testo fornito.
     */
    public void moderateText(Sentence sentence) {

        try {

            ModerateTextResponse response = new APIClient<ModerateTextResponse>()
                    .setAPIType(APIClient.RequestType.MODERATION)
                    .setSentenceToAPI(sentence.getSentence().toString())
                    .execute();


            // TODO scegliere categorie da mettere a posto
            // Toxic - Profanity - Insult - Threat - Identity Threat

            sentence.setToxicity(getModerationConfidenceByName(response.getModerationCategoriesList(), "Toxic"));
            sentence.setProfanity(getModerationConfidenceByName(response.getModerationCategoriesList(), "Profanity"));
            sentence.setInsult(getModerationConfidenceByName(response.getModerationCategoriesList(), "Insult"));
             sentence.setSexual(getModerationConfidenceByName(response.getModerationCategoriesList(), "Sexual"));
            sentence.setPolitics(getModerationConfidenceByName(response.getModerationCategoriesList(), "Politics"));
            response.getModerationCategoriesList().forEach(category -> System.out.println("Category: " + category.getName() + ", Confidence: " + category.getConfidence()));

        } catch (Exception e) {
            e.printStackTrace();
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