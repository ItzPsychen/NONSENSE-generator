package unipd.edids.userInterface;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.managers.LoggerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TaskManager {
    private static final Logger logger = LoggerManager.getInstance().getLogger(TaskManager.class);
    private static final List<Task<?>> runningTasks = new ArrayList<>();

    public static <T> void execute(Task<T> task, Consumer<T> onSuccess) {
        synchronized (runningTasks) {
            runningTasks.add(task);
        }
        task.setOnSucceeded(e -> {
            // Rimuovi task dall'elenco quando è completata
            removeTask(task);
            onSuccess.accept(task.getValue());
        });        // Gestisci task falliti
        task.setOnFailed(e -> {
            // Rimuovi task dall'elenco in caso di errore
            removeTask(task);
            Throwable ex = task.getException();
            String errorMessage = (ex != null) ? ex.getMessage() : "Unknown error occurred";

            logger.error("Task failed: {}", errorMessage);
            Platform.runLater(() -> showErrorDialog("Task Error", errorMessage));
        });

        task.setOnCancelled(e -> {
            // Rimuovi task dall'elenco quando è annullata
            removeTask(task);
            logger.info("Task was cancelled.");
        });

        // Esegui il task su un nuovo thread
        new Thread(task).start();
    }

    // Annulla tutte le task attive
    public static void cancelAllTasks() {
        synchronized (runningTasks) {
            for (Task<?> task : runningTasks) {
                if (task.isRunning()) {
                    task.cancel(); // Annulla la task
                }
            }
            runningTasks.clear(); // Pulisce la lista
        }
        logger.info("All tasks have been cancelled.");
    }

    // Rimuovi una task dall'elenco
    private static void removeTask(Task<?> task) {
        synchronized (runningTasks) {
            runningTasks.remove(task);
        }
    }

    // Helper per mostrare la finestra di dialogo dell'errore
    public static void showErrorDialog(String errorClass, String errorMessage) {
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