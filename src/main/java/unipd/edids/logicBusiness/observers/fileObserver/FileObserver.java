package unipd.edids.logicBusiness.observers.fileObserver;

/**
 * Observes changes in a specified file and triggers notifications for these events.
 *
 * <p>Responsibilities:
 * - Define a contract for observing and reacting to file changes.
 * - Enable the implementation of custom behavior when a file is modified.
 *
 * <p>Design Pattern:
 * - Observer Pattern: Acts as the Observer component, monitoring file changes and notifying implementers.
 */
public interface FileObserver {

    /**
     * Invoked when a monitored file changes to perform the appropriate response or action.
     *
     * @param filePath The file path of the modified file being observed.
     */
    void onFileChanged(String filePath);
}