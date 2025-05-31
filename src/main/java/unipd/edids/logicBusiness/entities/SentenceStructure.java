package unipd.edids.logicBusiness.entities;

import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;
import unipd.edids.logicBusiness.observers.configObserver.ConfigObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SentenceStructure implements ConfigObserver {
    private static final String DEFAULT_STRUCTURE = "[NOUN] [VERB] [NOUN]";
    private static SentenceStructure instance; // Singola istanza della classe
    private List<String> structures;


    // Costruttore privato per impedire l'istanziazione diretta
    private SentenceStructure() {
        structures = new ArrayList<>();
        loadStructures();
    }

    // Metodo pubblico e statico per ottenere l'istanza unica
    public static SentenceStructure getInstance() {
        if (instance == null) {
            instance = new SentenceStructure();
        }
        return instance;
    }

    private void loadStructures() {
        structures = FileManager.readFile(ConfigManager.getInstance().getProperty("sentence.structures"));
    }

    public String getRandomStructure() {
        if (structures.isEmpty()) return DEFAULT_STRUCTURE;
        Random random = new Random();
        return structures.get(random.nextInt(structures.size()));
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