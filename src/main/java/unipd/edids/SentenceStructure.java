package unipd.edids;

import java.io.*;
import java.nio.file.*;
import java.util.*;
//fix c'è un set non usato
public class SentenceStructure implements ConfigObserver {
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
            structures = FileManager.readFile(ConfigManager.getInstance().getProperty("sentence.structures"));
            structSet.addAll(structures);

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

    @Override
    public void onConfigChange(String key, String value) {
        if ("sentence.structures".equals(key)) {
            loadStructures();
        }
    }

    public List<String> getStructures() {
        return structures;
    }
}