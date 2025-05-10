package unipd.edids;

import com.google.cloud.language.v1.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AnalyzeSentenceService {
    private static final Logger logger = LogManager.getLogger(AnalyzeSentenceService.class);

    private Sentence temp;

    public AnalyzeSentenceService() {
        temp = new Sentence();
    }


    /**
     * Esegue l'analisi della sintassi di un testo.
     *
     * @param text il testo da analizzare.
     * @return una lista di tokens che rappresentano l'analisi della sintassi.
     */
    public Sentence analyzeSyntax(String text) {
        List<Token> tokens = fetchSyntaxTokens(text);
        temp.setStructure(new StringBuilder());
        for (Token token : tokens) {
            String word = token.getText().getContent();
            String pos = String.valueOf(token.getPartOfSpeech().getTag());
            logger.info("Parola: {}, Parte del discorso: {}", word, pos);
            switch (pos) {
                case "NOUN":
                    temp.getNouns().add(word);
                    temp.getStructure().append("[noun] ");
                    break;
                case "VERB":
                    temp.getVerbs().add(word);
                    temp.getStructure().append("[verb] ");
                    break;
                case "ADJ":
                    temp.getAdjectives().add(word);
                    temp.getStructure().append("[adjective] ");
                    break;
                default:
                    temp.getStructure().append(word).append(" ");
            }
        }
        temp.setStructure(new StringBuilder(temp.getStructure().toString().trim()));
        System.out.println(temp.getStructure());

        return temp;
    }
    public List<Token> fetchSyntaxTokens(String text) {
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