//package unipd.edids;
//
//import java.util.*;
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class Verb extends Word {
//    // the vocabulary is created only once
//    private static Set<String> vocabulary;
//    static {
//        try {
//            vocabulary = new HashSet<>(Files.readAllLines(Paths.get("./src/main/resources/verbs.txt")));
//        } catch (IOException e) {
//            e.printStackTrace();
//            vocabulary = new HashSet<>();
//        }
//    }
//
//    // class attributes                         TO ADD SOME OTHERS (maybe)
//    private String verbForm;
//    private String tense;
//    private boolean irregular;
//
//    // public constructor of the class
//    public Verb(String value) {
//        super(value);
//    }
//
//    // to set some information                  TO BE COMPLETED
//    @Override
//    protected void setAttributes() {
//
//    }
//
//    public String form() {
//        if (this.text.endsWith("ing")) return "+ing";
//        return (this.text.endsWith("ed") || this.irregular) ? "+ed" : "";
//    }
//
//    @Override
//    public boolean isInVocabulary() {
//        return vocabulary.contains(this.text);
//    }
//}


package unipd.edids.entities;

import unipd.edids.ConfigManager;
import unipd.edids.ConfigObserver;
import unipd.edids.FileManager;
import unipd.edids.strategies.FutureTenseStrategy;
import unipd.edids.strategies.TenseStrategy;

import java.util.Random;

public class Verb extends Word implements ConfigObserver {
    private static Verb instance;
    private TenseStrategy tenseStrategy;

    private Verb() {
        super(ConfigManager.getInstance().getProperty("verb.file"));

        // Registra questo oggetto come osservatore delle modifiche di configurazione
        ConfigManager.getInstance().addObserver(this);
        FileManager.addObserver(this);
    }

    public static Verb getInstance() {
        if (instance == null) {
            synchronized (Verb.class) {
                if (instance == null) {
                    instance = new Verb();
                }
            }
        }
        return instance;
    }

    @Override
    protected String getFilePath() {
        // Restituisce il percorso corrente del file dei sostantivi
        return this.filePath;
    }

    @Override
    public void onConfigChange(String key, String value) {
        if ("verb.file".equals(key)) {
            this.filePath = value;
            loadWords(this.filePath);
        }
    }
    public String getRandomWord() {
        System.out.println(tenseStrategy.getClass().getSimpleName());
        if (words.isEmpty()) return "undefined";
        Random random = new Random();
        return conjugate(words.get(random.nextInt(words.size())));
    }

    public String conjugate(String verb) {
        return this.tenseStrategy.conjugate(verb);
    }

    public void setTenseStrategy(TenseStrategy strategy) {
        this.tenseStrategy = strategy;
    }

    public TenseStrategy getTenseStrategy() {
        return tenseStrategy;
    }
}

