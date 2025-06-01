package unipd.edids;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.*;
import unipd.edids.logicBusiness.exceptions.MissingApiKeyException;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.LoggerManager;
import unipd.edids.logicBusiness.services.APIClient;
import unipd.edids.userInterface.FormController;
import unipd.edids.userInterface.SettingsController;
import unipd.edids.userInterface.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

import static unipd.edids.userInterface.TaskManager.showErrorDialog;

/**
 * The main application class extending the JavaFX Application, responsible for initializing and managing the application lifecycle.
 *
 * <p>Responsibilities:</p>
 * - Manages the application startup and shutdown process.
 * - Initializes and configures the primary UI stage and related components.
 * - Ensures application dependencies and configurations are properly set before proceeding.
 *
 * <p>Design Pattern:</p>
 * - Uses Singleton and Factory patterns:
 *   - Utilizes singleton instances for managing configurations and logging (e.g., LoggerManager, ConfigManager).
 *   - Uses Factory Method for creating and loading UI components (e.g., FXMLLoader).
 */
public class App extends Application {

    /**
     * Logger instance used for logging application-level events and behaviors within the App class.
     * Ensures centralized and consistent logging functionality.
     */
    private static final Logger logger = LoggerManager.getInstance().getLogger(App.class);


    /**
     * Starts the JavaFX application by initializing configurations, ensuring API Key configuration,
     * and setting up the main application window.
     *
     * @param primaryStage the primary stage for the JavaFX application
     * @throws Exception if any error occurs during application startup
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            logger.info("Starting the application");


            ConfigManager configManager = ConfigManager.getInstance();
            setSystemProperties(configManager);

            String apiKeyFile = getApiKeyFile(configManager);
            if (isApiKeyFileMissing(apiKeyFile)) {
                logger.warn("API Key file not configured. Opening settings window.");
                try {
                    openSettingsWindow();
                } catch (IOException e) {
                    logger.error("Error opening settings window: ", e);
                    showErrorDialog("Settings Error", "An error occurred while opening the settings window. Please try again.");
                    return;
                }

                apiKeyFile = getApiKeyFile(configManager);
                if (isApiKeyFileMissing(apiKeyFile)) {
                    handleMissingApiKey();
                    return;
                }
            }

            initializeMainWindow(primaryStage, configManager);
        }catch (Exception e){
            logger.error("Error during application startup: ", e);
        }
    }

    /**
     * Sets system properties required by the application.
     *
     * @param configManager the configuration manager used to retrieve environment variables and property values
     */
    private void setSystemProperties(ConfigManager configManager) {
        System.setProperty("LOG_LEVEL", configManager.getEnv("LOG_LEVEL"));
        System.setProperty("OUTPUT_LOGFILE", configManager.getProperty("output.logfile"));
    }

    /**
     * Retrieves the file path of the API key from the provided configuration manager.
     *
     * @param configManager the configuration manager instance used to fetch the property for the API key file.
     * @return the API key file path as a String if the property is defined and valid; otherwise, null.
     */
    private String getApiKeyFile(ConfigManager configManager) {
        try {
            return configManager.getProperty("api.key.file");
        } catch (MissingApiKeyException e) {
            logger.warn("api.key.file is not defined or is blank. Ignoring and proceeding.");
            return null;
        }
    }

    /**
     * Checks if the provided API key file path is missing or empty.
     *
     * @param apiKeyFile The file path of the API key to check.
     * @return true if the API key file path is null or empty, false otherwise.
     */
    private boolean isApiKeyFileMissing(String apiKeyFile) {
        return apiKeyFile == null || apiKeyFile.isEmpty();
    }

    /**
     * Path to the FXML file for the settings window.
     */
    private static final String SETTINGS_FXML_PATH = "/Settings.fxml";

    /**
     * Opens and displays the settings window of the application.
     * This method creates a new modal stage for the settings interface,
     * ensuring the calling process waits until the settings window is closed.
     *
     * @throws IOException if the settings window cannot be loaded.
     */
    private void openSettingsWindow() throws IOException {
        Stage settingsStage = createSettingsStage();
        settingsStage.showAndWait();
    }

    /**
     * Creates and configures a new Stage for displaying the settings window.
     *
     * @return A Stage configured for the settings window with appropriate modality and controller setup.
     * @throws IOException if the FXML file for the settings window cannot be loaded.
     */
    private Stage createSettingsStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SETTINGS_FXML_PATH));
        Parent settingsRoot = loader.load();
        SettingsController settingsController = loader.getController();

        Stage stage = new Stage();
        stage.setTitle("Settings");
        stage.setScene(new Scene(settingsRoot));
        stage.initModality(Modality.APPLICATION_MODAL);
        settingsController.setStage(stage);

        return stage;
    }

    /**
     * Handles the scenario when the API Key configuration is missing.
     * <p>
     * Logs an error indicating the mandatory requirement for the API Key configuration and prevents the application from starting.
     * Displays an error dialog prompting the user to configure the API Key in the settings.
     */
    private void handleMissingApiKey() {
        logger.error("API Key configuration is mandatory. The application will not start.");
        showErrorDialog("Missing Configuration", "API Key configuration file is mandatory. Please configure it in settings.");
    }

    /**
     * Initializes the main application window with the provided stage and configuration settings.
     *
     * @param primaryStage the primary stage of the application
     * @param configManager the configuration manager containing application settings
     * @throws IOException if an error occurs while loading the FXML file
     */
    private void initializeMainWindow(Stage primaryStage, ConfigManager configManager) throws IOException {
        logger.info("API Key file detected. Continuing application initialization.");
        AppManager appManager = new AppManager(configManager);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Form.fxml"));
        Parent root = loader.load();
        FormController controller = loader.getController();
        controller.setFacade(appManager);
        controller.setPrimaryStage(primaryStage);

        try {
            // Attempt to retrieve and add the icon
            String iconPath = Objects.requireNonNull(getClass().getResource("/icons/icon.png")).toString();
            primaryStage.getIcons().add(new Image(iconPath));
        } catch (NullPointerException e) {
            // Log a warning in case of a loading issue
            logger.warn("Failed to load the application icon: the path might be missing or incorrect.");
        }

        primaryStage.setTitle("NONSENSE Generator");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1200);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            handleApplicationClose(primaryStage);
        });
        primaryStage.show();
    }

    /**
     * Stops the application gracefully by performing cleanup operations.
     *
     * <p>
     * This method handles the following tasks:
     * - Cancels all running tasks through TaskManager.
     * - Closes any active API connections via APIClient.
     * - Logs errors encountered during shutdown.
     * - Ensures the JavaFX platform thread exits.
     * - Forces the termination of any remaining blocked threads.
     */
    @Override
    public void stop() {
        logger.info("Stopping the application...");
        try {
            TaskManager.cancelAllTasks(); // Existing method to close tasks
            APIClient.closeClient(); // Method to close any API connections
        } catch (Exception e) {
            logger.error("Error during shutdown: ", e);
        } finally {
            Platform.exit(); // Closes JavaFX thread
            System.exit(0); // Forces closure of any blocked threads
        }
    }

    /**
     * Handles the closure of the application gracefully, ensuring all tasks are canceled,
     * resources are released, and the application exits cleanly.
     *
     * @param primaryStage The primary stage of the JavaFX application to be closed.
     */
    private void handleApplicationClose(Stage primaryStage) {
        logger.info("Application is closing...");
        try {
            TaskManager.cancelAllTasks(); // Close tasks
            APIClient.closeClient(); // Close API client
            primaryStage.close();
            Platform.exit();
        } finally {
            logger.warn("Application has been closed forcefully.");
            System.exit(0); // Forces application closure
        }
    }


    /**
     * Entry point for the application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args); // Launches JavaFX application
    }
}