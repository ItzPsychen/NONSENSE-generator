package unipd.edids;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SentenceStructure {
    private static SentenceStructure instance; // Singola istanza della classe
    private List<String> structures;
    private final Set<String> structSet;

    // Costruttore privato per impedire l'istanziazione diretta
    private SentenceStructure() {
        structures = new ArrayList<>();
        structSet = new HashSet<>();
        loadStructures();
    }

    // Metodo pubblico e statico per ottenere l'istanza unica
    public static SentenceStructure getInstance() {
        if (instance == null) { // Creazione dell'istanza se non esiste ancora
            synchronized (SentenceStructure.class) { // Blocco synchronized per supporto multithreading
                if (instance == null) { // Verifica di doppia verifica per sicurezza
                    instance = new SentenceStructure();
                }
            }
        }
        return instance;
    }

    private void loadStructures() {
        try {
            structures = Files.readAllLines(Paths.get(ConfigManager.getInstance().getProperty("sentence.structures", "./src/main/resources/structures/sentenceStructures.txt")));
            structSet.addAll(structures);
        } catch (IOException e) {
            System.out.println("Errore nel caricamento delle sentence structures.");
            e.printStackTrace();
        }
    }

    public String getRandomStructure() {
        if (structures.isEmpty()) return "[NOUN] [VERB] [NOUN]";
        Random random = new Random();
        return structures.get(random.nextInt(structures.size()));
    }

    public void addNewStructure(String value) {
        if (structSet.contains(value)) return;
        structures.add(value);
        structSet.add(value);
    }
}