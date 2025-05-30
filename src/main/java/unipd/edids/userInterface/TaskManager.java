package unipd.edids.userInterface;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
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

        // Testo iniziale (informativo)
        Text generalText = new Text("A problem occurred during the execution of the task.\n\n");

        // Testo per il messaggio d'errore (grassetto e rosso)
        Text errorText = new Text(errorMessage);
        errorText.setStyle("-fx-fill: red; -fx-font-weight: bold;"); // Stile (rosso e grassetto)

        // TextFlow per combinare i due messaggi
        TextFlow textFlow = new TextFlow(generalText, errorText);

        // Imposta una larghezza massima per il TextFlow (il testo andrà a capo se supera questa larghezza)
        textFlow.setPrefWidth(400); // Cambia il valore per regolare la larghezza desiderata
        textFlow.setMaxWidth(400);
        textFlow.setMaxHeight(Region.USE_PREF_SIZE); // Comportamento fisso per l'altezza

        // Abilita il wrapping del testo (per andare a capo)
        generalText.wrappingWidthProperty().set(380); // Deve essere inferiore alla larghezza del TextFlow
        errorText.wrappingWidthProperty().set(380);

        // Imposta il TextFlow come contenuto del DialogPane
        alert.getDialogPane().setContent(textFlow);

        // Imposta una dimensione fissa per il DialogPane
        alert.getDialogPane().setPrefWidth(450);
        alert.getDialogPane().setPrefHeight(200);
        alert.getDialogPane().setMaxWidth(450); // Massima larghezza
        alert.getDialogPane().setMaxHeight(200); // Massima altezza

        // Mostra la finestra di dialogo
        alert.showAndWait();
    }
}