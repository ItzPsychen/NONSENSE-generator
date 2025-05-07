package unipd.edids;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.ModerateTextRequest;
import com.google.cloud.language.v1.ModerateTextResponse;

public class TextModerationService {

    /**
     * Analizza il livello di tossicità e modera il testo fornito.
     *
     * @param text il testo da moderare.
     * @return un oggetto `ModerateTextResponse` contenente i risultati della moderazione del testo.
     */
    public static ModerateTextResponse moderateText(String text) {
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
            return language.moderateText(request);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}