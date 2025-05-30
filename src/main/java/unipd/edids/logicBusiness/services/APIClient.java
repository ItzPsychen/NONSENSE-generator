package unipd.edids.logicBusiness.services;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.*;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.LoggerManager;
import unipd.edids.logicBusiness.observers.configObserver.ConfigObserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;

public class APIClient<T> implements ConfigObserver {

    private static final Logger logger = LoggerManager.getInstance().getLogger(APIClient.class);

    private String text;
    private RequestType type;

    // Il path della API key sarà dinamico
    private static String credentialsFilePath = "./src/main/resources/nonsense-generator-458709-f6e2fe62e727.json";

    // Singola istanza del client
    private static LanguageServiceClient instance;

    public enum RequestType {MODERATION, SYNTAX}

    @Override
    public void onConfigChange(String key, String value) {
        if ("api.key.file".equals(key)) {
            synchronized (APIClient.class) {
                // Aggiorno il path del file delle credenziali
                credentialsFilePath = value;

                // Chiudo il client esistente per forzare la ricreazione
                closeClient();
            }
        }
    }

    public APIClient<T> setSentenceToAPI(String text) {
        this.text = text;
        return this;
    }

    public APIClient<T> setAPIType(RequestType type) {
        this.type = type;
        return this;
    }

    @SuppressWarnings("unchecked")
    public T execute() {
        isServiceAvailable();
        LanguageServiceClient client = getInstance();
        Document doc = Document.newBuilder()
                .setContent(text)
                .setType(Document.Type.PLAIN_TEXT)
                .build();


            return switch (type) {
                case MODERATION -> (T) client.moderateText(
                        ModerateTextRequest.newBuilder().setDocument(doc).build()
                );
                case SYNTAX -> (T) client.analyzeSyntax(
                        AnalyzeSyntaxRequest.newBuilder()
                                .setDocument(doc)
                                .setEncodingType(EncodingType.UTF16)
                                .build()
                );
            };

    }

    // Metodo per ottenere un'istanza singleton del client
    private static synchronized LanguageServiceClient getInstance() {
        if (instance == null) {
            try {
                // Recupera dinamicamente il path dal ConfigManager
                credentialsFilePath = ConfigManager.getInstance().getProperty("api.key.file");
                if (credentialsFilePath == null || credentialsFilePath.isBlank()) {
                    throw new IllegalStateException("The path to the API key is not configured! Please configure it via File > Settings.");
                }

                // Crea l'istanza del client utilizzando il path
                instance = LanguageServiceClient.create(
                        LanguageServiceSettings.newBuilder()
                                .setCredentialsProvider(
                                        () -> ServiceAccountCredentials.fromStream(new FileInputStream(credentialsFilePath))
                                )
                                .build()
                );
            } catch (java.net.UnknownHostException e) {
                // Gestione specifica per problemi di rete/DNS
                throw new RuntimeException("Unable to resolve the API service's hostname. Please check your internet connection. This could be a temporary DNS issue. Original error: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException("Error creating the LanguageServiceClient:\n" + e.getMessage(), e);
            } catch (Exception e) {
                // Log e nuova eccezione in caso di errore di connessione (es. DNS)
                throw new RuntimeException("Generic error occurred while creating the client or connecting to the service:\n" + e.getMessage(), e);
            }
        }
        return instance;
    }

    // Metodo per chiudere l'istanza del client
    public static synchronized void closeClient() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }


    public void isServiceAvailable() {
        try {
            InetAddress.getByName("language.googleapis.com");
        } catch (Exception e) {
            throw new RuntimeException("Service Unavailable: Unable to resolve the hostname for the API. Please check your internet connection or DNS settings.", e);
        }
    }
}