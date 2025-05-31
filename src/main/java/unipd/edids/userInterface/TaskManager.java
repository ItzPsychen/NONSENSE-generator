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

/**
 * Manages the execution of asynchronous tasks with centralized lifecycle management, logging, and error handling functionality.
 *
 * <p>Responsibilities:</p>
 * - Executes asynchronous tasks and monitors their lifecycle.
 * - Logs task events such as completion, failure, and cancellation.
 * - Provides error dialogs for failed tasks.
 * - Allows cancellation of all running tasks.
 *
 * <p>Design Pattern:</p>
 * - Implements the Singleton design pattern for logger management.
 */
public class TaskManager {
    /**
     * Logger instance for monitoring and debugging TaskManager operations.
     * Provides centralized logging for the class.
     */
    private static final Logger logger = LoggerManager.getInstance().getLogger(TaskManager.class);
    /**
     * Stores the list of tasks currently being executed in the application.
     */
    private static final List<Task<?>> runningTasks = new ArrayList<>();

    /**
     * Executes a given task in a new thread and manages its success, failure,
     * or cancellation events.
     *
     * @param <T>       The type of the task's result.
     * @param task      The task to be executed.
     * @param onSuccess A consumer to handle the task's result upon successful completion.
     */
    public static <T> void execute(Task<T> task, Consumer<T> onSuccess) {
        synchronized (runningTasks) {
            runningTasks.add(task);
        }
        task.setOnSucceeded(e -> {
            // Remove task from list when completed
            removeTask(task);
            onSuccess.accept(task.getValue());
        });        // Handle failed tasks
        task.setOnFailed(e -> {
            // Remove task from list in case of error
            removeTask(task);
            Throwable ex = task.getException();
            String errorMessage = (ex != null) ? ex.getMessage() : "Unknown error occurred";

            logger.error("Task failed: {}", errorMessage);
            Platform.runLater(() -> showErrorDialog("Task Error", errorMessage));
        });

        task.setOnCancelled(e -> {
            // Remove task from list when canceled
            removeTask(task);
            logger.info("Task was cancelled.");
        });

        // Execute task on new thread
        new Thread(task).start();
    }

    /**
     * Cancels all currently running tasks managed by the system.
     * <p>
     * This method iterates through the list of active tasks, identifies tasks
     * that are still running, and cancels them. After canceling, the list of
     * tasks is cleared. A log message is generated to indicate that all tasks
     * have been canceled.
     */
    public static void cancelAllTasks() {
        synchronized (runningTasks) {
            for (Task<?> task : runningTasks) {
                if (task.isRunning()) {
                    task.cancel(); // Cancel the task
                }
            }
            runningTasks.clear(); // Clear the list
        }
        logger.info("All tasks have been cancelled.");
    }

    /**
     * <p>
     * TaskManager class responsibilities:
     * - Manages the execution, cancellation, and removal of tasks.
     * - Implements synchronization for thread-safe operations.
     * <p>
     * Design Pattern:
     * - Uses the Singleton-like design for managing a shared task list via static methods and fields.
     */
    // Remove a task from the list
    private static void removeTask(Task<?> task) {
        synchronized (runningTasks) {
            runningTasks.remove(task);
        }
    }

    /**
     * Displays an error dialog with a given error class and message content.
     *
     * @param errorClass   The class or category of the error, displayed as the header of the dialog.
     * @param errorMessage The specific error message, displayed prominently in the dialog content.
     */
    // Helper to show error dialog
    public static void showErrorDialog(String errorClass, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("WATCH OUT!");
        alert.setHeaderText(errorClass);

        // Initial text (informative)
        Text generalText = new Text("A problem occurred during the execution of the task.\n\n");

        // Error message text (bold and red)
        Text errorText = new Text(errorMessage);
        errorText.setStyle("-fx-fill: red; -fx-font-weight: bold;"); // Style (red and bold)

        // TextFlow to combine both messages
        TextFlow textFlow = new TextFlow(generalText, errorText);

        // Set maximum width for TextFlow (text will wrap if it exceeds this width)
        textFlow.setPrefWidth(400); // Change value to adjust desired width
        textFlow.setMaxWidth(400);
        textFlow.setMaxHeight(Region.USE_PREF_SIZE); // Fixed behavior for height

        // Enable text wrapping
        generalText.wrappingWidthProperty().set(380); // Must be less than TextFlow width
        errorText.wrappingWidthProperty().set(380);

        // Set TextFlow as DialogPane content
        alert.getDialogPane().setContent(textFlow);

        // Set fixed size for DialogPane
        alert.getDialogPane().setPrefWidth(450);
        alert.getDialogPane().setPrefHeight(200);
        alert.getDialogPane().setMaxWidth(450); // Maximum width
        alert.getDialogPane().setMaxHeight(200); // Maximum height

        // Show dialog window
        alert.showAndWait();
    }
}