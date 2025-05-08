package unipd.edids;

import com.google.cloud.language.v1.*;

import java.util.List;

public class  TextModerationService {

    /**
     * Analizza il livello di tossicità e modera il testo fornito.
     *
     * @param text il testo da moderare.
     * @return un oggetto `ModerateTextResponse` contenente i risultati della moderazione del testo.
     */
    public static List<ClassificationCategory> moderateText(String text) {
        LanguageServiceClient language = APIClient.getInstance(); // Ottieni il client singleton

        try {
            // Crea il documento da moderare
            Document doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

            // Crea la richiesta di moderazione
            ModerateTextRequest request = ModerateTextRequest.newBuilder()
                    .setDocument(doc)
                    .build();



            // Recupera la risposta dalla ModerateText API
            ModerateTextResponse response = language.moderateText(request);

            return response.getModerationCategoriesList();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}