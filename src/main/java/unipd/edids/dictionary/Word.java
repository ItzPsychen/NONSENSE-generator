//package unipd.edids;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public abstract class Word {
//    // shared attributes across all word types
//    protected String text;
//    public static String input = "";
//    // Constructor
//    public Word(String value) {
//        this.text = value;
//        setAttributes();
//    }
//
//    // getter for text
//    public String getText() {
//        return this.text;
//    }
//
//    // setter for text
//    public void setText(String value) {
//        this.text = value;
//        setAttributes();
//    }
//
//    // abstract method implemented by subclasses
//    protected abstract void setAttributes();
//
//    // placeholder for vocabulary check
//    public abstract boolean isInVocabulary();
//}
package unipd.edids.dictionary;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class Word {
    protected List<String> words;

    // Costruttore protetto
    protected Word() {
        words = new ArrayList<>();
        loadWords(getFilePath());
    }

    // Metodo astratto per definire il path del file specifico
    protected abstract String getFilePath();

    // Caricamento delle parole dal file specificato
    private void loadWords(String filePath) {
        try {
            words = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Errore nel caricamento del file: " + filePath);
            e.printStackTrace();
        }

    }

    // Restituisce una parola casuale dalla lista caricata
    public String getRandomWord() {
        if (words.isEmpty()) return "undefined";
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }
}
