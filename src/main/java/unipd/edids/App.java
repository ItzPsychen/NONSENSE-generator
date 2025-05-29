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
////        Form form = new Form();
//
//
//    }
//}

package unipd.edids;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

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
        System.setProperty("LOG_LEVEL", ConfigManager.getInstance().getEnv("LOG_LEVEL", "info"));
        System.setProperty("OUTPUT_LOGFILE", ConfigManager.getInstance().getProperty("output.logfile", "logs/app.log"));
        System.setProperty("GENERATED_NONSENSE", ConfigManager.getInstance().getProperty("generated.save.file", "logs/output/generated.txt"));
        System.setProperty("DETAILS_NONSENSE", ConfigManager.getInstance().getProperty("analyzed.save.file", "logs/output/details.txt"));

        logger.info("Starting the application");

        // Verifica se l'API key è configurata
        ConfigManager configManager = ConfigManager.getInstance();
        String apiKeyFile = configManager.getProperty("api.key.file", null);

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
            apiKeyFile = configManager.getProperty("api.key.file", null);
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
        primaryStage.getIcons().add(new Image(configManager.getProperty("icon.main", null)));
        primaryStage.setTitle("NONSENSE Generator");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1200);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Avvia l'applicazione
        launch(args);
    }
}

