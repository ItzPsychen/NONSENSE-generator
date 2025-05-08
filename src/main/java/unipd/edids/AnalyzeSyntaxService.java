package unipd.edids;

import com.google.cloud.language.v1.*;

import java.util.List;

public class AnalyzeSyntaxService {

    /**
     * Esegue l'analisi della sintassi di un testo.
     *
     * @param text il testo da analizzare.
     * @return una lista di tokens che rappresentano l'analisi della sintassi.
     */
    public static List<Token> analyzeSyntax(String text) {
        LanguageServiceClient language = APIClient.getInstance(); // Ottieni il client singleton

        try {
            // Creazione del documento da analizzare
            Document doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

            // Creazione della richiesta per l'analisi della sintassi
            AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF16) // Specifica la codifica del testo
                    .build();

            // Esegui l'analisi
            AnalyzeSyntaxResponse response = language.analyzeSyntax(request);

            // Restituisci i token analizzati
            return response.getTokensList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}