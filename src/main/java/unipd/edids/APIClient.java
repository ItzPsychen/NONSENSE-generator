package unipd.edids;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;

import java.io.FileInputStream;
import java.io.IOException;

public class APIClient {

    // Singola istanza del client
    private static LanguageServiceClient instance;

    // Percorso del file di credenziali
    private static final String CREDENTIALS_FILE_PATH = "./src/main/resources/nonsense-generator-458709-f6e2fe62e727.json";

    // Costruttore privato per impedire l'istanziamento diretto
    public APIClient() {}

    // Metodo per creare od ottenere l'istanza del client
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
}