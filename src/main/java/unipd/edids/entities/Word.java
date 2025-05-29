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
package unipd.edids.entities;
import unipd.edids.FileManager;
import unipd.edids.FileObserver;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class Word implements FileObserver {
    protected List<String> words;
    protected String filePath; // Spostato qui

    // Costruttore protetto
    protected Word(String filePath) {
        this.filePath = filePath;

        words = new ArrayList<>();
        loadWords(filePath); // Carica le parole direttamente
    }

    // Metodo astratto per definire il path del file specifico
    protected abstract String getFilePath();

    // Caricamento delle parole dal file specificato
    protected void loadWords(String filePath) {

            words = FileManager.readFile(filePath);


    }

    // Restituisce una parola casuale dalla lista caricata
    public String getRandomWord() {
        if (words.isEmpty()) return "undefined";
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    @Override
    public void onFileChanged(String fileChanged) {
        if (this.filePath.equals(fileChanged)) {
            loadWords(this.filePath);
        }
    }



}
