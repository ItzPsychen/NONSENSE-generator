package unipd.edids.logicBusiness.entities;
import unipd.edids.logicBusiness.managers.FileManager;
import unipd.edids.logicBusiness.observers.fileObserver.FileObserver;

import java.util.*;

/**
 * The Word class is an abstract representation of a word list
 * that is dynamically loaded from a file and observed for changes.
 *
 * <p>Responsibilities:
 * - Manages a list of words loaded from a file.
 * - Observes changes in the associated file and updates the word list accordingly.
 * - Provides functionality to retrieve a random word from the list.
 *
 * <p>Design Pattern:
 * - Observer Pattern: Listens to file change events to automatically reload the word list.
 */
public abstract class Word implements FileObserver {

    /**
     * A protected list holding the words that are dynamically managed within the class.
     */
    protected List<String> words;

    /**
     * The file path to the source file containing words for this Word instance.
     */
    protected String filePath;

    /**
     * Initializes a Word instance with the provided file path and loads words from the file.
     *
     * @param filePath The file path used to load the list of words.
     */
    protected Word(String filePath) {
        this.filePath = filePath;
        words = new ArrayList<>();
        loadWords(filePath);
    }

    /**
     * Loads a list of words from a specified file into the 'words' attribute.
     *
     * @param filePath the path of the file containing the words to load
     */
    protected void loadWords(String filePath) {
            words = FileManager.readFile(filePath);
    }

    /**
     * Retrieves the file path associated with the current word list.
     *
     * @return the path of the file containing the word list
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Retrieves a random word from the list of words. If the list is empty,
     * returns a default value "undefined".
     *
     * @return A randomly selected word from the list of words, or "undefined" if the list is empty.
     */
    public String getRandomWord() {
        if (words.isEmpty()) return "undefined";
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    /**
     * Handles file change events and reloads the words from the updated file
     * if it matches the managed file path.
     *
     * @param fileChanged The path of the file that has been changed.
     */
    @Override
    public void onFileChanged(String fileChanged) {
        if (this.filePath.equals(fileChanged)) {
            loadWords(this.filePath);
        }
    }
}
