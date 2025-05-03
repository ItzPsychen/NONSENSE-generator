package unipd.edids;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Adjective extends Word {
    // the vocabulary is created only once
    private static Set<String> vocabulary;
    static {
        try {
            vocabulary = new HashSet<>(Files.readAllLines(Paths.get("adjectives.txt")));
        } catch (IOException e) {
            e.printStackTrace();
            vocabulary = new HashSet<>();
        }
    }

    // class attributes                         TO ADD SOME OTHERS (maybe)
    private String text;

    // public constructor of the class
    public Adjective(String value) {
        super(value);
    }

    // to set some information                  TO BE COMPLETED
    protected void setAttributes() {

    }

    public boolean isInVocabulary() {
        return vocabulary.contains(this.text);
    }
}
