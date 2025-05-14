package unipd.edids;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Sentence {
    private static final Logger logger = LogManager.getLogger(Sentence.class);


    private StringBuilder sentence;                // frase originale o generata
    private StringBuilder structure;                // frase con i placeholder tipo "The [NOUN] [VERB]..."
    private StringBuilder syntaxTree;


    private List<String> nouns;
    private List<String> verbs;
    private List<String> adjectives;

    // Costruttori, getter, setter

    public Sentence() {
        this.sentence = new StringBuilder();
        this.structure = new StringBuilder();
        this.syntaxTree = new StringBuilder();
        this.nouns = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.adjectives = new ArrayList<>();
    }


    public StringBuilder getSyntaxTree() {
        return syntaxTree;
    }

    public void setSyntaxTree(StringBuilder syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    public StringBuilder getSentence() {
        return sentence;
    }

    public StringBuilder getStructure() {
        return structure;
    }

    public void setSentence(StringBuilder sentence) {
        this.sentence = sentence;
    }

    public void setStructure(StringBuilder structure) {
        this.structure = structure;
    }

    public void setNouns(List<String> nouns) {
        this.nouns = nouns;
    }

    public void setVerbs(List<String> verbs) {
        this.verbs = verbs;
    }

    public void setAdjectives(List<String> adjectives) {
        this.adjectives = adjectives;
    }

    public List<String> getNouns() {
        return nouns;
    }

    public List<String> getVerbs() {
        return verbs;
    }

    public List<String> getAdjectives() {
        return adjectives;
    }


}
