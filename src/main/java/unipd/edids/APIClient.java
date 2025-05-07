package unipd.edids;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.*;

import java.io.FileInputStream;
import java.io.IOException;

public class APIClient {

    // Singola istanza del client
    private static LanguageServiceClient instance;

    // Percorso del file di credenziali
    private static final String CREDENTIALS_FILE_PATH = "./src/main/resources/nonsense-generator-458709-f6e2fe62e727.json";

    // Costruttore privato per il Singleton
    private APIClient() {}

    // Metodo per creare o ottenere l'istanza del client
    public static synchronized LanguageServiceClient getInstance() {
        if (instance == null) {
            try {
                instance = LanguageServiceClient.create(
                        LanguageServiceSettings.newBuilder()
                                .setCredentialsProvider(
                                        () -> ServiceAccountCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                                )
                                .build()
                );
            } catch (IOException e) {
                throw new RuntimeException("Errore nella creazione del LanguageServiceClient", e);
            }
        }
        return instance;
    }

    // Metodo per chiudere il client
    public static synchronized void closeClient() {
        if (instance != null) {
            instance.close();
            instance = null; // Ripristina l'istanza
        }
    }

    // Metodo che centralizza la richiesta e l'analisi
    public static String requestSentence(String text) {
        LanguageServiceClient language = getInstance(); // Ottieni il client

        try {
            // Creazione del documento
            Document doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

            // Creazione della richiesta
            AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF16)
                    .build();

            // Esegui l'analisi della sintassi
            AnalyzeSyntaxResponse response = language.analyzeSyntax(request);

            // Costruzione di una rappresentazione leggibile dei risultati
            StringBuilder sb = new StringBuilder();
            for (Token token : response.getTokensList()) {
                sb.append(String.format("Text: %s, Part of Speech: %s%n",
                        token.getText().getContent(),
                        token.getPartOfSpeech().getTag()));
            }

            // Ritorna i risultati come stringa
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Errore durante l'analisi della sintassi.";
        }
    }
}