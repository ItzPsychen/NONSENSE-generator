package unipd.edids.logicBusiness.entities;

import edu.stanford.nlp.trees.Tree;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SentenceTest {

    @Test
    void testConstructorDefault() {
        Sentence sentence = new Sentence();
        assertNotNull(sentence.getSentence(), "Default sentence StringBuilder should not be null");
        assertNotNull(sentence.getStructure(), "Default structure StringBuilder should not be null");
        assertNotNull(sentence.getSyntaxTree(), "Default syntax tree should not be null");
        assertNotNull(sentence.getNouns(), "Default nouns list should not be null");
        assertNotNull(sentence.getVerbs(), "Default verbs list should not be null");
        assertNotNull(sentence.getAdjectives(), "Default adjectives list should not be null");

        assertEquals(0, sentence.getNouns().size(), "Default noun list size should be 0");
        assertEquals(0, sentence.getVerbs().size(), "Default verb list size should be 0");
        assertEquals(0, sentence.getAdjectives().size(), "Default adjective list size should be 0");
    }

    @Test
    void testConstructorWithValidText() {
        Sentence sentence = new Sentence("Hello world!");
        assertEquals("Hello world!", sentence.getSentence().toString(), "Sentence text should match the provided text");
    }

    @Test
    void testConstructorWithNullText() {
        assertThrows(IllegalArgumentException.class, () -> new Sentence(null), "Constructor should throw exception if null text is provided");
    }

    @Test
    void testSetSyntaxTreeWithValidTree() {
        Sentence sentence = new Sentence();
        Tree tree = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
        sentence.setSyntaxTree(tree);
        assertNotNull(sentence.getSyntaxTree(), "Syntax tree should be updated successfully");
    }

    @Test
    void testSetSyntaxTreeWithNull() {
        Sentence sentence = new Sentence();
        assertThrows(IllegalArgumentException.class, () -> sentence.setSyntaxTree(null), "Should throw exception when null syntax tree is set");
    }

    @Test
    void testSetToxicity() {
        Sentence sentence = new Sentence();
        sentence.setToxicity(0.5);
        assertEquals(0.5, sentence.getToxicity(), "Toxicity value should be set to 0.5");

        assertThrows(IllegalArgumentException.class, () -> sentence.setToxicity(-0.1), "Should throw exception if toxicity is less than 0");
        assertThrows(IllegalArgumentException.class, () -> sentence.setToxicity(1.1), "Should throw exception if toxicity is greater than 1");
    }

    @Test
    void testSetProfanity() {
        Sentence sentence = new Sentence();
        sentence.setProfanity(0.3);
        assertEquals(0.3, sentence.getProfanity(), "Profanity value should be set to 0.3");

        assertThrows(IllegalArgumentException.class, () -> sentence.setProfanity(-0.1), "Should throw exception if profanity is less than 0");
        assertThrows(IllegalArgumentException.class, () -> sentence.setProfanity(1.1), "Should throw exception if profanity is greater than 1");
    }

    @Test
    void testSetInsult() {
        Sentence sentence = new Sentence();
        sentence.setInsult(0.7);
        assertEquals(0.7, sentence.getInsult(), "Insult value should be set to 0.7");

        assertThrows(IllegalArgumentException.class, () -> sentence.setInsult(-0.1), "Should throw exception if insult is less than 0");
        assertThrows(IllegalArgumentException.class, () -> sentence.setInsult(1.1), "Should throw exception if insult is greater than 1");
    }

    @Test
    void testSetSexual() {
        Sentence sentence = new Sentence();
        sentence.setSexual(0.2);
        assertEquals(0.2, sentence.getSexual(), "Sexual value should be set to 0.2");

        assertThrows(IllegalArgumentException.class, () -> sentence.setSexual(-0.1), "Should throw exception if sexual level is less than 0");
        assertThrows(IllegalArgumentException.class, () -> sentence.setSexual(1.1), "Should throw exception if sexual level is greater than 1");
    }

    @Test
    void testSetPolitics() {
        Sentence sentence = new Sentence();
        sentence.setPolitics(0.4);
        assertEquals(0.4, sentence.getPolitics(), "Politics value should be set to 0.4");

        assertThrows(IllegalArgumentException.class, () -> sentence.setPolitics(-0.1), "Should throw exception if politics level is less than 0");
        assertThrows(IllegalArgumentException.class, () -> sentence.setPolitics(1.1), "Should throw exception if politics level is greater than 1");
    }

    @Test
    void testSetNouns() {
        Sentence sentence = new Sentence();
        List<String> nouns = Arrays.asList("Cat", "Dog", "Bird");
        sentence.setNouns(nouns);
        assertEquals(nouns, sentence.getNouns(), "Nouns should be set correctly");

        assertThrows(IllegalArgumentException.class, () -> sentence.setNouns(null), "Should throw exception if nouns list is null");
    }

    @Test
    void testSetVerbs() {
        Sentence sentence = new Sentence();
        List<String> verbs = Arrays.asList("Run", "Jump", "Swim");
        sentence.setVerbs(verbs);
        assertEquals(verbs, sentence.getVerbs(), "Verbs should be set correctly");

        assertThrows(IllegalArgumentException.class, () -> sentence.setVerbs(null), "Should throw exception if verbs list is null");
    }

    @Test
    void testSetAdjectives() {
        Sentence sentence = new Sentence();
        List<String> adjectives = Arrays.asList("Fast", "Strong", "Beautiful");
        sentence.setAdjectives(adjectives);
        assertEquals(adjectives, sentence.getAdjectives(), "Adjectives should be set correctly");

        assertThrows(IllegalArgumentException.class, () -> sentence.setAdjectives(null), "Should throw exception if adjectives list is null");
    }

    @Test
    void testSetSentence() {
        Sentence sentence = new Sentence();
        StringBuilder text = new StringBuilder("Hello World!");
        sentence.setSentence(text);
        assertEquals(text.toString(), sentence.getSentence().toString(), "Sentence text should match");

        assertThrows(IllegalArgumentException.class, () -> sentence.setSentence(null), "Should throw exception if sentence text is null");
    }

    @Test
    void testSetStructure() {
        Sentence sentence = new Sentence();
        StringBuilder structure = new StringBuilder("Noun-Verb");
        sentence.setStructure(structure);
        assertEquals(structure.toString(), sentence.getStructure().toString(), "Structure text should match");

        assertThrows(IllegalArgumentException.class, () -> sentence.setStructure(null), "Should throw exception if structure is null");
    }
}