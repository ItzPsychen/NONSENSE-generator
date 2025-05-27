package unipd.edids;

import edu.stanford.nlp.trees.SimpleTree;
import edu.stanford.nlp.trees.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Sentence {
    private static final Logger logger = LogManager.getLogger(Sentence.class);

    private StringBuilder sentence;                 // frase originale o generata
    private StringBuilder structure;                // frase con i placeholder tipo "The [NOUN] [VERB]..."
    private Tree syntaxTree;

    private double toxicity;
    private double profanity;
    private double insult;
    private double threat;
    private double identityThreat;

    private List<String> nouns;
    private List<String> verbs;
    private List<String> adjectives;

    // Costruttori, getter, setter

    public Sentence() {
        this.sentence = new StringBuilder();
        this.structure = new StringBuilder();
        this.syntaxTree = new SimpleTree();
        this.toxicity = 0.0;
        this.nouns = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.adjectives = new ArrayList<>();
    }

    public Sentence(String text) {
        this.sentence = new StringBuilder(text);
    }

    public Tree getSyntaxTree() { return syntaxTree; }
    public void setSyntaxTree(Tree syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    public StringBuilder getSentence() {
        return sentence;
    }
    public StringBuilder getStructure() {
        return structure;
    }

    public void setSentence(StringBuilder sentence) { this.sentence = sentence; }
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

    public List<String> getNouns() { return nouns; }
    public List<String> getVerbs() {
        return verbs;
    }
    public List<String> getAdjectives() {
        return adjectives;
    }

    public void setToxicity(double value) { this.toxicity = value; }
    public void setProfanity(double value) { this.profanity = value; }
    public void setInsult(double value) { this.insult = value; }
    public void setThreat(double value) { this.threat = value; }
    public void setIdentityThreat(double value) { this.identityThreat = value; }

    public double getToxicity() { return this.toxicity; }
    public double getProfanity() { return this.profanity; }
    public double getInsult() { return this.insult; }
    public double getThreat() { return this.threat; }
    public double getIdentityThreat() { return this.identityThreat; }

    public boolean isValid() {
        return true;

        // if (this.toxicity > 0.3 || this.profanity > 0.3) return false;
        // if (this.threat > 0.5 || this.identityThreat > 0.5) return false;
        // return this.insult < 0.4;
    }
}
