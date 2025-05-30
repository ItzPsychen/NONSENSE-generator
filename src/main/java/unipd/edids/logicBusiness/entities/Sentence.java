package unipd.edids.logicBusiness.entities;

import edu.stanford.nlp.trees.SimpleTree;
import edu.stanford.nlp.trees.Tree;
import java.util.ArrayList;
import java.util.List;

public class Sentence {

    private StringBuilder sentence;
    private StringBuilder structure;
    private Tree syntaxTree;

    private double toxicity;
    private double profanity;
    private double insult;
    private double sexual;
    private double politics;

    private List<String> nouns;
    private List<String> verbs;
    private List<String> adjectives;

    public Sentence() {
        this.sentence = new StringBuilder();
        this.structure = new StringBuilder();
        this.syntaxTree = new SimpleTree();
        this.nouns = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.adjectives = new ArrayList<>();
    }

    public Sentence(String text) {
        this.sentence = new StringBuilder(text);
        this.structure = new StringBuilder();
        this.syntaxTree = new SimpleTree();
        this.nouns = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.adjectives = new ArrayList<>();
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
    public void setSexual(double value) { this.sexual = value; }
    public void setPolitics(double value) { this.politics = value; }

    public double getToxicity() { return this.toxicity; }
    public double getProfanity() { return this.profanity; }
    public double getInsult() { return this.insult; }
    public double getSexual() { return this.sexual; }
    public double getPolitics() { return this.politics; }

    @Override
    public String toString() {
        return sentence + "\n" + structure +"\n" + syntaxTree.toString();
    }
}
