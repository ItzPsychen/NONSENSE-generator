package unipd.edids.logicBusiness.managers;

import unipd.edids.logicBusiness.observers.fileObserver.FileObserver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods for working with files and managing file observers.
 *
 * <p>Responsibilities:
 * - Provide methods for file manipulation: reading, writing, appending, and deleting files.
 * - Maintain and manage a list of FileObserver objects to monitor file-related events.
 * - Notify observers whenever specific file operations are performed.
 *
 * <p>Design Pattern:
 * - Observer Pattern: Serves as the Subject component, managing observer registration and notifying them upon file changes.
 */
public class FileManager {

    /**
     * Manages registered observers for monitoring file-related events.
     *
     * <p>Responsibilities:
     * - Maintain a list of registered FileObservers.
     * - Enable registration and removal of FileObservers.
     * - Notify all registered FileObservers of file events.
     *
     * <p>Design Pattern:
     * - Observer Pattern: Acts as the Subject component, notifying observers of file changes.
     */
    // List of registered observers (static)
    private static final List<FileObserver> observers = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FileManager() {
    }

    /**
     * Appends a new line to a vocabulary file. If the file does not exist, it is created.
     * Notifies observers of the file change after appending the line.
     *
     * @param filePath the path of the vocabulary file
     * @param newLine  the line to append to the file
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public static void appendLineToVocabularyFile(String filePath, String newLine) throws IOException {
        // Check if a file doesn't exist and if so, create it
        Path path = Paths.get(filePath);
        if (Files.notExists(path)) {
            Files.createFile(path);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine(); // Ensure a new line is always added
            writer.write(newLine);
        }
        // Notify observers of the change
        notifyObservers(filePath);
    }

    /**
     * Appends a new line of text to a specified file, creating the file if it does not exist.
     *
     * @param filePath The path of the file to which the text should be appended.
     * @param newLine  The line of text to append to the file.
     */
    public static void appendLineToSavingFile(String filePath, String newLine) {
        try {
            // Check if a file doesn't exist and if so, create it
            Path path = Paths.get(filePath);
            if (Files.notExists(path)) {
                Files.createFile(path);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.newLine(); // Ensure a new line is always added
                writer.write(newLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the contents of a file and returns a list of non-empty lines.
     *
     * @param filePath The path of the file to be read.
     * @return A list of strings representing the non-empty lines in the file.
     * @throws RuntimeException If an I/O error occurs during file reading.
     */
    public static List<String> readFile(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath)).stream()
                    .filter(line -> !line.trim().isEmpty())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file: " + filePath, e);
        }
    }

    /**
     * Adds a FileObserver instance to the list of observers.
     *
     * @param observer The FileObserver instance to be added for monitoring file changes.
     */
    public static void addObserver(FileObserver observer) {
        observers.add(observer);
    }

    /**
     * Notifies all registered observers about a change in the specified file.
     *
     * @param filePath The file path of the modified file to notify observers about.
     */
    private static void notifyObservers(String filePath) {
        for (FileObserver observer : observers) {
            observer.onFileChanged(filePath);
        }
    }

    /**
     * Deletes a file at the specified path if it exists.
     * Throws an exception if deletion fails or the file does not exist.
     *
     * @param filePath the path of the file to delete
     * @throws IOException if the file cannot be deleted or does not exist
     */
    public static void deleteFile(String filePath) throws IOException {
        if (!Files.deleteIfExists(Paths.get(filePath))) {
            throw new IOException("File deletion failed or file does not exist: " + filePath);
        }
    }


}