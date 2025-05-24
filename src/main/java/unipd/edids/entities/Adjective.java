//package unipd.edids;
//
//import java.util.*;
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class Adjective extends Word {
//    // the vocabulary is created only once
//    private static Set<String> vocabulary;
//    static {
//        try {
//            vocabulary = new HashSet<>(Files.readAllLines(Paths.get("./src/main/resources/adjectives.txt")));
//        } catch (IOException e) {
//            e.printStackTrace();
//            vocabulary = new HashSet<>();
//        }
//    }
//
//    // class attributes                         TO ADD SOME OTHERS (maybe)
//
//    // public constructor of the class
//    public Adjective(String value) {
//        super(value);
//    }
//
//    // to set some information                  TO BE COMPLETED
//    protected void setAttributes() {
//
//    }
//
//    public boolean isInVocabulary() {
//        return vocabulary.contains(this.text);
//    }
//}
package unipd.edids.entities;

import unipd.edids.ConfigManager;

public class Adjective extends Word {
    private static Adjective instance;

    private Adjective() {
        super();
    }

    public static Adjective getInstance() {
        if (instance == null) {
            synchronized (Adjective.class) {
                if (instance == null) {
                    instance = new Adjective();
                }
            }
        }
        return instance;
    }

    @Override
    protected String getFilePath() {
        return ConfigManager.getInstance().getProperty("adjective.file","./src/main/resources/words/adjectives.txt");
    }
}