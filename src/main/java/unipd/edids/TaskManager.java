package unipd.edids;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class TaskManager {
    private static final Logger logger = LoggerManager.getInstance().getLogger(TaskManager.class);

    public static <T> void execute(Task<T> task, Consumer<T> onSuccess) {
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        // Gestisci task falliti
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            String errorMessage = (ex != null) ? ex.getMessage() : "Unknown error occurred";
            String errorClass = (ex != null) ? ex.getClass().toString() : "Unknown error class";

            // Log dell'errore (per debugging)
            logger.error(ex);
            System.err.println("Task failed: " + errorMessage);

            // Mostra un messaggio di errore nell'UI
            Platform.runLater(() -> showErrorDialog(errorClass, errorMessage));
        });

        // Esegui il task su un nuovo thread
        new Thread(task).start();
    }

    // Helper per mostrare la finestra di dialogo dell'errore
    private static void showErrorDialog(String errorClass, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("WATCH OUT!");
        alert.setHeaderText(errorClass);
        Text generalText = new Text("A problem occurred during the execution of the task:\n");

        // Testo per l'errorMessage (grassetto e rosso)
        Text errorText = new Text(errorMessage);
        errorText.setStyle("-fx-fill: red; -fx-font-weight: bold;"); // Rosso e grassetto

        // TextFlow per combinare i messaggi
        TextFlow textFlow = new TextFlow(generalText, errorText);

        // Imposta il TextFlow come contenuto della finestra di dialogo
        alert.getDialogPane().setContent(textFlow);

        // Mostra la finestra di dialogo
        alert.showAndWait();
    }
}