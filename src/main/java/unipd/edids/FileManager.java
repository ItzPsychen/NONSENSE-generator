package unipd.edids;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    // Instanza unica della classe (Singleton)
    private static FileManager instance;

    // Lista degli observer registrati
    private final List<FileObserver> observers = new ArrayList<>();

    /**
     * Costruttore privato per impedire la creazione di istanze esterne.
     */
    private FileManager() {
        // Costruttore privato
    }

    /**
     * Metodo per avere l'istanza unica di FileManager.
     *
     * @return L'istanza unica di FileManager.
     */
    public static FileManager getInstance() {
        if (instance == null) {
            synchronized (FileManager.class) {
                if (instance == null) {
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }

    /**
     * Metodo per concatenare una nuova linea a un file esistente.
     *
     * @param filePath Il path del file in cui scrivere.
     * @param newLine  La linea da aggiungere.
     * @throws IOException Se ci sono problemi con la scrittura del file.
     */
    public void appendLineToVocabularyFile(String filePath, String newLine) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine(); // Assicura che si aggiunga sempre una nuova linea
            writer.write(newLine);
        }
        // Notifica gli observer del cambiamento
        notifyObservers(filePath);
    }


    public void appendLineToSavingFile(String filePath, String newLine) throws IOException {
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
    public List<String> readFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath)).stream()
                .filter(line -> !line.trim().isEmpty())
                .toList();
    }



    /**
     * Aggiunge un observer alla lista degli observer registrati.
     *
     * @param observer L'istanza che implementa l'interfaccia FileObserver.
     */
    public void addObserver(FileObserver observer) {
        observers.add(observer);
    }

    /**
     * Rimuove un observer dalla lista.
     *
     * @param observer L'istanza da rimuovere.
     */
    public void removeObserver(FileObserver observer) {
        observers.remove(observer);
    }


    private void notifyObservers(String filePath) {
        for (FileObserver observer : observers) {
            observer.onFileChanged(filePath);
        }
    }
}