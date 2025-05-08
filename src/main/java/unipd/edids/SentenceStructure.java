//package unipd.edids;
//
//import java.util.*;
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class Structure {
//    // the structure list is created only once
//    private static Set<Structure> structures;
//    static {
//        try {
//            structures = new HashSet<>();
//            List<String> lines = Files.readAllLines(Paths.get("./src/main/resources/sentenceStructures.txt"));
//            for (int i = 0; i < lines.size(); i = i + 3) {
//                String structure = lines.get(i);
//                String[] tmp = lines.get(i + 1).split(" ");
//
//                // int array for counting (nouns, verbs, adjectives)
//                int[] counters = new int[tmp.length];
//                for (int j = 0; j < tmp.length; ++j) {
//                    counters[j] = Integer.parseInt(tmp[j]);
//                }
//
//                structures.add(new Structure(structure, counters));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // class attributes                         TO ADD SOME OTHERS (maybe)
//    private String text;
//    private int nouns;
//    private int verbs;
//    private int adjectives;
//    private int adverbs;
//
//    // public constructor of the class
//    public Structure(String value, int[] counters) {
//        this.text = value;
//        this.nouns = counters[0];
//        this.verbs = counters[1];
//        this.adjectives = counters[2];
//        this.adverbs = counters[3];
//    }
//
//    // public constructor with only String value
//    public Structure(String value) {
//        this.text = value;
//        setAttributes();
//    }
//
//    // copy constructor
//    public Structure(Structure structure) {
//        this.text = structure.getText();
//        this.nouns = structure.getNouns();
//        this.verbs = structure.getVerbs();
//        this.adjectives = structure.getAdjectives();
//        this.adverbs = structure.getAdverbs();
//    }
//
//    // setter for the text value
//    public String getText() {
//        return this.text;
//    }
//
//    // getter for the text value
//    void setText(String value) {
//        this.text = value;
//        setAttributes();
//    }
//
//    int getNouns() { return this.nouns; }
//    int getVerbs() { return this.verbs; }
//    int getAdjectives() { return this.adjectives; }
//    int getAdverbs() { return this.adverbs; }
//
//    // to set some information                  TO BE COMPLETED
//    void setAttributes() {
//        List<String> words = new ArrayList<>(Arrays.asList(this.text.split(" ")));
//        int nounCount = 0, verbCount = 0, adjCount = 0, advCount = 0;
//
//        // loop through each word in the list
//        for (String word : words) {
//            if (new Noun(word).isInVocabulary()) nounCount++;
//            else if (new Verb(word).isInVocabulary()) verbCount++;
//            else if (new Adjective(word).isInVocabulary()) adjCount++;
//            else if (new Adverb(word).isInVocabulary()) advCount++;
//        }
//    }
//
//    public Structure getNewRandom() {
//        List<Structure> validStructures = new ArrayList<>();
//        for (Structure structure : structures) {
//            System.out.println(structure);
//        }
//        for (Structure current : structures) {
//            // if (compareCount(current)) {
//                validStructures.add(current);
//            // }
//        }
//
//        // randomly choose a new Structure
//        if (validStructures.isEmpty()) {
//            System.out.println("BOMBA");
//        return null;
//        }
//        Random r = new Random();
//        return validStructures.get(r.nextInt(validStructures.size()));
//    }
//
//    public boolean compareCount(Structure toCompare) {
//        if (this.nouns != toCompare.getNouns() || this.verbs != toCompare.getVerbs()) return false;
//        return this.adjectives == toCompare.getAdjectives() && this.adverbs == toCompare.getAdverbs();
//    }
//
//    public boolean isStored() {
//        return structures.contains(this);
//    }
//}
package unipd.edids;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SentenceStructure {
    private List<String> structures;

    public SentenceStructure() {
        structures = new ArrayList<>();
        loadStructures();
    }

    private void loadStructures() {
        try {
            structures = Files.readAllLines(Paths.get("./src/main/resources/sentenceStructures.txt"));
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
}