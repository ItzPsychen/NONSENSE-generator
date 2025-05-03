package unipd.edids;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Structure {
    // the structure list is created only once
    private static List<Structure> structures = new ArrayList<>();
    static {
        try {
            List<String> lines = Files.readAllLines(Paths.get("sentenceStructures.txt"));
            for (int i = 0; i < lines.size(); i = i + 3) {
                String structure = lines.get(i);
                String[] tmp = lines.get(i + 1).split(" ");

                // int array for counting (nouns, verbs, adjectives)
                int[] counters = new int[tmp.length];
                for (int j = 0; j < tmp.length; ++j) {
                    counters[j] = Integer.parseInt(tmp[j]);
                }

                structures.add(new Structure(structure, counters));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // class attributes                         TO ADD SOME OTHERS (maybe)
    private String text;
    private int nouns;
    private int verbs;
    private int adjectives;

    // public constructor of the class
    public Structure(String value, int[] counters) {
        this.text = value;
        this.nouns = counters[0];
        this.verbs = counters[1];
        this.adjectives = counters[2];
    }

    // setter for the text value
    String getText() {
        return this.text;
    }

    // getter for the text value
    void setText(String value) {
        this.text = value;
        setAttributes();
    }

    // to set some information                  TO BE COMPLETED
    void setAttributes() {
        List<String> words = new ArrayList<>(Arrays.asList(this.text.split(" ")));
        int nounCount = 0, verbCount = 0, adjCount = 0;

        // loop through each word in the list
        for (String word : words) {
            Noun noun = new Noun(word);
            Verb verb = new Verb(word);
            Adjective adj = new Adjective(word);

            // increment the counters based on the type
            if (noun.isInVocabulary()) nounCount++;
            else if (verb.isInVocabulary()) verbCount++;
            else if (adj.isInVocabulary()) adjCount++;
        }
    }

    public boolean isStored() {
        return structures.contains(this.text);
    }
}
