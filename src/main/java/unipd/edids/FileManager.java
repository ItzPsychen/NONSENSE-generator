package unipd.edids;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    // Lista degli observer registrati (statica)
    private static final List<FileObserver> observers = new ArrayList<>();

    /**
     * Costruttore privato per impedire l'istanza della classe.
     */
    private FileManager() {
        // Previene l'istanza della classe
    }

    /**
     * Metodo per concatenare una nuova linea a un file esistente.
     *
     * @param filePath Il path del file in cui scrivere.
     * @param newLine  La linea da aggiungere.
     * @throws IOException Se ci sono problemi con la scrittura del file.
     */
    public static void appendLineToVocabularyFile(String filePath, String newLine) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine(); // Assicura che si aggiunga sempre una nuova linea
            writer.write(newLine);
        }
        // Notifica gli observer del cambiamento
        notifyObservers(filePath);
    }

    public static void appendLineToSavingFile(String filePath, String newLine) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine(); // Assicura che si aggiunga sempre una nuova linea
            writer.write(newLine);
        }
    }

    /**
     * Metodo per leggere tutte le linee da un file.
     *
     * @param filePath Il path del file da leggere.
     * @return Una lista di stringhe, una per ogni riga del file.
     * @throws IOException Se ci sono problemi con la lettura del file.
     */
    public static List<String> readFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath)).stream()
                .filter(line -> !line.trim().isEmpty())
                .toList();
    }

    /**
     * Aggiunge un observer alla lista degli observer registrati.
     *
     * @param observer L'istanza che implementa l'interfaccia FileObserver.
     */
    public static void addObserver(FileObserver observer) {
        observers.add(observer);
    }

    /**
     * Rimuove un observer dalla lista.
     *
     * @param observer L'istanza da rimuovere.
     */
    public static void removeObserver(FileObserver observer) {
        observers.remove(observer);
    }

    private static void notifyObservers(String filePath) {
        for (FileObserver observer : observers) {
            observer.onFileChanged(filePath);
        }
    }
}