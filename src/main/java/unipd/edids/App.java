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
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.setProperty("LOG_LEVEL", ConfigManager.getInstance().getEnv("LOG_LEVEL", "info"));
        System.setProperty("OUTPUT_LOGFILE", ConfigManager.getInstance().getProperty("output.logfile", "logs/app.log"));

        // Carica la vista dalla risorsa FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Form.fxml"));
        Parent root = loader.load();


        // Imposta le proprietà della finestra
        primaryStage.setTitle("NONSENSE Generator");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Avvia l'applicazione
        launch(args);
    }
}

