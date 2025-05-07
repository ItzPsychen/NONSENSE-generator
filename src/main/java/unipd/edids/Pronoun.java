package unipd.edids;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Pronoun extends Word {
    private static Set<String> vocabulary;
    static {
        try {
            vocabulary = new HashSet<>(Files.readAllLines(Paths.get("./src/main/resources/nouns.txt")));
        } catch (IOException e) {
            e.printStackTrace();
            vocabulary = new HashSet<>();
        }
    }

    public Pronoun(String value) {
        super(value);
    }

    @Override
    protected void setAttributes() {

    }

    public boolean isInVocabulary() {
        return vocabulary.contains(this.text);
    }
}
