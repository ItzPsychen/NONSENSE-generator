package unipd.edids.logicBusiness.entities;

import edu.stanford.nlp.trees.SimpleTree;
import edu.stanford.nlp.trees.Tree;
import java.util.ArrayList;
import java.util.List;

/**
 * The Sentence class is responsible for representing and analyzing a sentence's structure, content, and attributes.
 *
 * <p> Responsibilities:
 * - Encapsulates a sentence as text and its associated syntactic structure.
 * - Maintains attributes such as syntactic tree, nouns, verbs, adjectives, and various content metrics (e.g., toxicity, profanity).
 * - Provides methods for managing and analyzing syntactic and linguistic features of the sentence.
 * - Offers configuration for content metrics with value constraints.
 *
 * <p> Design Patterns:
 * - Builder-like approach: Utilizes StringBuilders for constructing sentence and structure data.
 */
public class Sentence {

    /**
     * Stores the textual content of the sentence being processed.
     */
    private StringBuilder sentence;
    /**
     * Represents the structural arrangement of a sentence.
     */
    private StringBuilder structure;
    /**
     * Represents the syntax tree of a sentence, encapsulating its syntactic structure.
     */
    private Tree syntaxTree;

    /**
     * Represents the level of toxicity associated with the sentence.
     */
    private double toxicity;
    /**
     * Represents the level of profanity detected in a sentence.
     * The value is a numerical score, typically in a defined range.
     */
    private double profanity;
    /**
     * Represents the degree of an insulting tone or language score within a sentence.
     */
    private double insult;
    /**
     * Indicates the degree of sexual content or connotation in the sentence.
     */
    private double sexual;
    /**
     * Represents the degree to which a sentence is associated with political content.
     */
    private double politics;

    /**
     * List of identified nouns in the sentence.
     */
    private List<String> nouns;
    /**
     * Represents a list of verbs extracted or utilized in the sentence structure.
     */
    private List<String> verbs;
    /**
     * A list containing adjectives used within the sentence.
     */
    private List<String> adjectives;

    /**
     * Initializes a new Sentence instance with default values.
     */
    public Sentence() {
        this.sentence = new StringBuilder();
        this.structure = new StringBuilder();
        this.syntaxTree = new SimpleTree();
        this.nouns = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.adjectives = new ArrayList<>();
    }

    /**
     * Initializes a new Sentence instance with the provided text.
     */
    public Sentence(String text) {
        if (text == null) {
            throw new IllegalArgumentException("The text of the sentence cannot be null");
        }
        this.sentence = new StringBuilder(text);
        this.structure = new StringBuilder();
        this.syntaxTree = new SimpleTree();
        this.nouns = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.adjectives = new ArrayList<>();
    }

    /**
     * Retrieves the syntax tree representation of the sentence.
     *
     * @return the syntax tree of the sentence.
     */
    public Tree getSyntaxTree() {
        return syntaxTree;
    }

    /**
     * Updates the syntax tree representation of the sentence.
     *
     * @param syntaxTree The syntax tree to be set. Must not be null.
     * @throws IllegalArgumentException If the provided syntax tree is null.
     */
    public void setSyntaxTree(Tree syntaxTree) {
        if (syntaxTree == null) {
            throw new IllegalArgumentException("Syntax tree cannot be null");
        }
        this.syntaxTree = syntaxTree;
    }

    /**
     * Retrieves the sentence represented as a StringBuilder object.
     *
     * @return the sentence as a StringBuilder instance
     */
    public StringBuilder getSentence() {
        return sentence;
    }

    /**
     * Retrieves the structure of the sentence.
     *
     * @return a StringBuilder object representing the sentence structure.
     */
    public StringBuilder getStructure() {
        return structure;
    }

    /**
     * Sets the sentence content for the Sentence class, ensuring it is not null.
     *
     * @param sentence The StringBuilder instance representing the sentence content.
     *                 Must not be null.
     * @throws IllegalArgumentException if the provided sentence is null.
     */
    public void setSentence(StringBuilder sentence) {
        if (sentence == null) {
            throw new IllegalArgumentException("Sentence cannot be null");
        }
        this.sentence = sentence;
    }

    /**
     * Sets the structure of the sentence.
     *
     * @param structure The structure of the sentence, represented as a StringBuilder. Cannot be null.
     * @throws IllegalArgumentException if the provided structure is null.
     */
    public void setStructure(StringBuilder structure) {
        if (structure == null) {
            throw new IllegalArgumentException("Structure cannot be null");
        }
        this.structure = structure;
    }

    /**
     * Sets the list of nouns for the current sentence.
     *
     * @param nouns The list of nouns to be set. Must not be null.
     * @throws IllegalArgumentException If the provided list of nouns is null.
     */
    public void setNouns(List<String> nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException("Nouns list cannot be null");
        }
        this.nouns = nouns;
    }

    /**
     * Updates the list of verbs associated with the sentence.
     *
     * @param verbs A list of verbs to set. Cannot be null.
     * @throws IllegalArgumentException If the provided list of verbs is null.
     */
    public void setVerbs(List<String> verbs) {
        if (verbs == null) {
            throw new IllegalArgumentException("Verbs list cannot be null");
        }
        this.verbs = verbs;
    }

    /**
     * Sets the list of adjectives for the Sentence.
     * Ensures that the provided list is non-null before assignment.
     *
     * @param adjectives the list of adjectives to set; must not be null
     * @throws IllegalArgumentException if the provided list is null
     */
    public void setAdjectives(List<String> adjectives) {
        if (adjectives == null) {
            throw new IllegalArgumentException("Adjectives list cannot be null");
        }
        this.adjectives = adjectives;
    }

    /**
     * Retrieves the list of nouns from the sentence.
     *
     * @return a list of nouns contained in the sentence.
     */
    public List<String> getNouns() {
        return nouns;
    }

    /**
     * Retrieves the list of verbs associated with the sentence.
     *
     * @return a list of strings representing the verbs in the sentence.
     */
    public List<String> getVerbs() {
        return verbs;
    }

    /**
     * Retrieves the list of adjectives associated with the Sentence class.
     *
     * @return a List of Strings containing the adjectives.
     */
    public List<String> getAdjectives() {
        return adjectives;
    }

    /**
     * Sets the toxicity level for the sentence. The value must be between 0 and 1.
     *
     * @param value A double representing the toxicity level, constrained between 0 and 1.
     * @throws IllegalArgumentException If the provided value is outside the range [0, 1].
     */
    public void setToxicity(double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Toxicity value must be between 0 and 1");
        }
        this.toxicity = value;
    }

    /**
     * Sets the profanity score for this sentence.
     * Validates that the provided value is between 0 and 1 (inclusive).
     *
     * @param value The profanity score to set. Must be a double between 0 and 1.
     * @throws IllegalArgumentException if the value is outside the valid range.
     */
    public void setProfanity(double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Profanity value must be between 0 and 1");
        }
        this.profanity = value;
    }

    /**
     * Sets the insult level for the sentence.
     *
     * @param value A double representing the insult level, must be between 0 and 1 inclusive.
     * @throws IllegalArgumentException if the provided value is not within the range [0, 1].
     */
    public void setInsult(double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Insult value must be between 0 and 1");
        }
        this.insult = value;
    }

    /**
     * Sets the level of sexual content in the sentence.
     * The value must be between 0 and 1, inclusive.
     *
     * @param value The level of sexual content, a double value ranging from 0 to 1.
     * @throws IllegalArgumentException if the provided value is not between 0 and 1.
     */
    public void setSexual(double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Sexual value must be between 0 and 1");
        }
        this.sexual = value;
    }

    /**
     * Sets the politics value for the sentence, ensuring it falls within a valid range.
     *
     * @param value The politics value to set, which must be between 0 and 1.
     * @throws IllegalArgumentException if the provided value is not within the valid range [0, 1].
     */
    public void setPolitics(double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Politics value must be between 0 and 1");
        }
        this.politics = value;
    }

    /**
     * Retrieves the toxicity level of the sentence.
     *
     * @return the toxicity value as a double.
     */
    public double getToxicity() {
        return this.toxicity;
    }

    /**
     * Retrieves the profanity score associated with the current sentence instance.
     *
     * @return the profanity score as a double value.
     */
    public double getProfanity() {
        return this.profanity;
    }

    /**
     * Retrieves the insult score of the sentence.
     *
     * @return the insult score as a double.
     */
    public double getInsult() {
        return this.insult;
    }

    /**
     * Retrieves the sexual content assessment score of the sentence.
     *
     * @return a double representing the sexual content score.
     */
    public double getSexual() {
        return this.sexual;
    }

    /**
     * Retrieves the politics score associated with the sentence.
     *
     * @return the politics score as a double.
     */
    public double getPolitics() {
        return this.politics;
    }

    /**
     * Converts the object to its string representation.
     *
     * @return A string consisting of the sentence, structure, and syntax tree representations, separated by newline characters.
     */
    @Override
    public String toString() {
        return sentence + "\n" + structure + "\n" + syntaxTree.toString();
    }
}
