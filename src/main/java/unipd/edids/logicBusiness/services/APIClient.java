package unipd.edids.logicBusiness.services;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.*;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.exceptions.MissingApiKeyException;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.LoggerManager;
import unipd.edids.logicBusiness.observers.configObserver.ConfigObserver;

import java.io.FileInputStream;
import java.net.InetAddress;

/**
 * * Represents a client for handling API interactions with flexible request types, maintaining configurations,
 * * and enabling seamless execution. Implements observer pattern to handle configuration changes.
 *
 * <p>Class Responsibilities:
 * - Handles communication with an external language-processing API.
 * - Abstracts the logic for service setup, request execution, and managing configurations.
 * - Monitors configuration changes dynamically.
 *
 * <p>Design Patterns Used:
 * - Singleton: Ensures a single instance of the LanguageServiceClient.
 * - Observer: Implements ConfigObserver to respond to configuration changes.
 */
public class APIClient<T> implements ConfigObserver {

    /**
     * Static logger instance specific to the APIClient class.
      */
    private static final Logger logger = LoggerManager.getInstance().getLogger(APIClient.class);
    /**
     * The endpoint for accessing Google's Natural Language API service.
     */
    private static final String SERVICE_ENDPOINT = "language.googleapis.com";

    /**
     * Stores the file path for API credentials retrieved from configuration properties.
     */
    private static String credentialsFilePath;
    /**
     * Singleton instance of the LanguageServiceClient for managing language analysis operations.
     * Ensures thread-safe lazy instantiation using double-checked locking.
     */
    private static volatile LanguageServiceClient instance;

    /**
     * Represents the sentence to interact with the API. Used for storing or processing API-related input text.
     */
    private String sentence;

    /**
     * Represents the type of API request to be executed by the client.
     */
    private RequestType requestType;


    /**
     * Creates a new instance of the APIClient class.
     * Subscribes to configuration changes via the ConfigObserver interface.
     */
    public APIClient() {
        ConfigManager.getInstance().addObserver(this);
        try {
            credentialsFilePath = ConfigManager.getInstance().getProperty("api.key.file");
        }catch (MissingApiKeyException e){
            logger.error("Impossible to initialize APIClient: {}", e.getMessage());
            throw new MissingApiKeyException("Impossible to initialize APIClient: " + e.getMessage());
        }

    }

    /**
     * Retrieves the singleton instance of the LanguageServiceClient.
     * Implements the Singleton design pattern to ensure a single instance is created and reused.
     *
     * @return A singleton instance of the LanguageServiceClient.
     */
    static LanguageServiceClient getInstance() {
        logger.debug("Getting LanguageServiceClient instance");
        if (instance == null) {
            logger.debug("LanguageServiceClient instance is null, creating a new one");
            synchronized (APIClient.class) {
                if (instance == null) {
                    instance = createLanguageServiceClient();
                }
            }
        }
        logger.debug("Returning LanguageServiceClient instance");
        return instance;
    }

    /**
     * Creates and configures an instance of {@code LanguageServiceClient}.
     *
     * @return a properly initialized instance of {@code LanguageServiceClient}.
     * @throws IllegalStateException if the credentials file path is not configured or invalid.
     * @throws RuntimeException if an error occurs during the creation of the {@code LanguageServiceClient}.
     */
    private static LanguageServiceClient createLanguageServiceClient() {

        if (credentialsFilePath == null || credentialsFilePath.isBlank()) {
            String errorMsg = "API key path is not configured! Please configure it via File > Settings.";
            logger.error(errorMsg);
            throw new MissingApiKeyException(errorMsg);
        }

        try {
            logger.debug("Creating LanguageServiceClient with credentials from: {}", credentialsFilePath);
            LanguageServiceClient client = LanguageServiceClient.create(
                    LanguageServiceSettings.newBuilder()
                            .setCredentialsProvider(() ->
                                    ServiceAccountCredentials.fromStream(new FileInputStream(credentialsFilePath)))
                            .build()
            );
            logger.info("LanguageServiceClient successfully created");
            return client;
        } catch (Exception e) {
            String errorMsg = "Error creating the LanguageServiceClient: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Closes the singleton instance of the `LanguageServiceClient` if it exists.
     *
     * <p> If a `LanguageServiceClient` instance is currently active, this method ensures it is properly closed,
     * releasing any resources it holds and setting the instance reference to null. If no instance is present,
     * the method logs that no action was needed.
     */
    public static synchronized void closeClient() {
        logger.debug("Attempting to close LanguageServiceClient");
        if (instance != null) {
            logger.info("Closing LanguageServiceClient instance");
            instance.close();
            instance = null;
            logger.debug("LanguageServiceClient instance closed and set to null");
        } else {
            logger.debug("No LanguageServiceClient instance to close");
        }
    }

    /**
     * Handles configuration changes by updating the specified key-value pair and performing related
     * operations if necessary.
     *
     * @param key   the configuration key that has changed
     * @param value the new value assigned to the configuration key
     */
    @Override
    public void onConfigChange(String key, String value) {
        logger.debug("Configuration change detected for key: {}", key);
        if ("api.key.file".equals(key)) {
            logger.info("API credentials file path changed to: {}", value);
            synchronized (APIClient.class) {
                credentialsFilePath = value;
                closeClient();
                logger.debug("Client closed due to credentials path change");
            }
        }
    }

    /**
     * Sets the sentence to be sent to the API for further processing.
     *
     * @param sentence the text that will be provided to the API as input
     * @return the current instance of {@code APIClient<T>} for method chaining
     */
    public APIClient<T> setSentenceToAPI(String sentence) {
        this.sentence = sentence;
        return this;
    }

    /**
     * Sets the type of API request to be executed.
     *
     * @param requestType the type of request to be executed by the API client.
     * @return the current instance of APIClient with the updated request type.
     */
    public APIClient<T> setAPIType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    /**
     * Executes an API request based on the current {@code requestType} and analyzes the given text document.
     * Handles service availability check and delegates the processing logic to the {@code execute} method
     * of the respective {@code RequestType}.
     *
     * @return The result of the API processing, returned as a generic type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T execute() {
        logger.debug("Executing API request of type: {}", requestType);
        isServiceAvailable();
        LanguageServiceClient client = getInstance();
        Document document = Document.newBuilder()
                .setContent(sentence)
                .setType(Document.Type.PLAIN_TEXT)
                .build();

        logger.debug("Created document with content length: {}", sentence.length());

        // Delego la logica al metodo `execute` dell'enum
        T result = (T) requestType.execute(client, document);
        logger.debug("API request executed successfully");
        return result;
    }


    /**
     * Checks connectivity to the configured service endpoint by performing DNS resolution.
     * Throws an exception if the DNS resolution fails.
     */
    public void isServiceAvailable() {
        try {
            logger.debug("Checking connectivity to service endpoint: {}", SERVICE_ENDPOINT);
            InetAddress address = InetAddress.getByName(SERVICE_ENDPOINT);
            logger.debug("Resolved address: {}", address.getHostAddress());

        } catch (Exception e) {
            String errorMsg = "DNS resolution failed for the hostname: " + SERVICE_ENDPOINT + ". Please check if the hostname is correct and verify your network connection.";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);

        }

    }

    /**
     * Retrieves the current request type set for the API client.
     *
     * @return the {@link RequestType} currently configured for the API client.
     */
    public RequestType getRequestType() {
        return requestType;
    }

    public enum RequestType {
        MODERATION {
            @Override
            public Object execute(LanguageServiceClient client, Document document) {
                return client.moderateText(
                        ModerateTextRequest.newBuilder().setDocument(document).build()
                );
            }
        },
        SYNTAX {
            @Override
            public Object execute(LanguageServiceClient client, Document document) {
                return client.analyzeSyntax(
                        AnalyzeSyntaxRequest.newBuilder()
                                .setDocument(document)
                                .setEncodingType(EncodingType.UTF16)
                                .build()
                );
            }
        };

        /**
         * Executes an action corresponding to the specific request type on the provided client and document.
         *
         * @param client the LanguageServiceClient used to perform the operation.
         * @param document the Document instance representing the input data for the operation.
         * @return the result object of the operation, specific to the executed request type.
         */
        public abstract Object execute(LanguageServiceClient client, Document document);
    }
}