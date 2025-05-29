//package unipd.edids;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.core.config.Configurator;
//
//import java.io.*;
//import java.lang.*;
//
//public class App {
//    private static final Logger logger = LogManager.getLogger(App.class);
//    public static void main(String[] args) throws IOException {
//
//        logger.info("The window is opening...");
//        // Creo un'istanza di Graphic (il resto del codice relativo a Graphic rimane lo stesso)
//        Graphic G = new Graphic();
//        logger.info("Have fun :)");
//
/// /        Form form = new Form();
//
//
//    }
//}

package unipd.edids;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

import static unipd.edids.TaskManager.showErrorDialog;

public class App extends Application {

    private static final Logger logger = LoggerManager.getInstance().getLogger(App.class);


    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting the application");

        //Fix primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon_image.png")));
        //TODO instanziare il facade e passarlo tramite dependency injection al Controller
        // Oppure renderlo singleton
        // AL MOMENTO C'È UNA DEPENDENCY INJECTION MANUALE

        //TODO inizializzare Logger e altri manager

        //TODO are those necessary?
        System.setProperty("LOG_LEVEL", ConfigManager.getInstance().getEnv("LOG_LEVEL"));
        System.setProperty("OUTPUT_LOGFILE", ConfigManager.getInstance().getProperty("output.logfile"));
        System.setProperty("GENERATED_NONSENSE", ConfigManager.getInstance().getProperty("generated.save.file"));
        System.setProperty("DETAILS_NONSENSE", ConfigManager.getInstance().getProperty("analyzed.save.file"));

        logger.info("Starting the application");

        // Verifica se l'API key è configurata
        ConfigManager configManager = ConfigManager.getInstance();
        String apiKeyFile = null;
        try {
            apiKeyFile = ConfigManager.getInstance().getProperty("api.key.file");
        } catch (IllegalArgumentException e) {
            // Ignora questa eccezione e logga un messaggio
            logger.warn("api.key.file is not defined or is blank. Ignoring and proceeding.");
        }
        if (apiKeyFile == null || apiKeyFile.isEmpty()) {
            logger.warn("API Key file not configured. Opening settings window.");

            // Apri la finestra delle impostazioni in modalità modale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Settings.fxml"));
            Parent settingsRoot = loader.load();

            SettingsController settingsController = loader.getController();
            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.setScene(new Scene(settingsRoot));
            settingsStage.initModality(Modality.APPLICATION_MODAL); // Finestra modale
            settingsController.setStage(settingsStage); // Passa lo stage al controller
            settingsStage.showAndWait();

            // Dopo la chiusura della finestra, verifica di nuovo l'API key
            try {
                apiKeyFile = ConfigManager.getInstance().getProperty("api.key.file");
            } catch (IllegalArgumentException e) {
                // Ignora questa eccezione e logga un messaggio
                logger.warn("api.key.file is not defined or is blank. Ignoring and proceeding.");
            }
            if (apiKeyFile == null || apiKeyFile.isEmpty()) {
                logger.error("API Key configuration is mandatory. The application will not start.");
                showErrorDialog("Missing Configuration", "API Key configuration file is mandatory. Please configure it in settings.");
                return; // Torna senza terminare bruscamente il processo
            }
        }

        // Inizializzazione dell'app principale
        logger.info("API Key file detected. Continuing application initialization.");

        AppManager appManager = new AppManager();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Form.fxml"));
        Parent root = loader.load();

        FormController controller = loader.getController();
        controller.setFacade(appManager);
        controller.setPrimaryStage(primaryStage);

        // Imposta le proprietà della finestra principale
        primaryStage.getIcons().add(new Image(configManager.getProperty("icon.main")));
        primaryStage.setTitle("NONSENSE Generator");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1200);
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Previene la chiusura automatica
            handleApplicationClose(primaryStage); // Effettua l'azione di chiusura
        });
        primaryStage.show();
    }

    /**
     * Metodo chiamato automaticamente alla chiusura dell'applicazione.
     */
    @Override
    public void stop() {
        logger.info("Stopping the application...");
        try {
            TaskManager.cancelAllTasks(); // Metodo esistente per chiudere i task
            APIClient.closeClient(); // Metodo per chiudere eventuali connessioni API
        } catch (Exception e) {
            logger.error("Error during shutdown: ", e);
        } finally {
            Platform.exit(); // Chiude il thread JavaFX
            System.exit(0); // Forza la chiusura di eventuali thread bloccati
        }
    }

    /**
     * Gestione della chiusura dell'applicazione.
     *
     * @param primaryStage Finestra principale da chiudere
     */
    private void handleApplicationClose(Stage primaryStage) {
        logger.info("Application is closing...");
        try {
            TaskManager.cancelAllTasks(); // Chiudi i task
            APIClient.closeClient(); // Chiudi il client API
            primaryStage.close();
            Platform.exit();
        } finally {
            logger.warn("Application has been closed forcefully.");
            System.exit(0); // Forza la chiusura dell'applicazione
        }
    }



    /**
     * Metodo principale per avviare l'applicazione.
     *
     * @param args Argomenti del programma
     */
    public static void main(String[] args) {
        launch(args); // Avvia l'applicazione JavaFX
    }
}