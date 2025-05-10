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


package unipd.edids.dictionary;

import unipd.edids.ConfigManager;

public class Verb extends Word {
    private static Verb instance;

    private Verb() {
        super();
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
        return ConfigManager.getInstance().getProperty("verb.file","./src/main/resources/verbs.txt");
    }
}

