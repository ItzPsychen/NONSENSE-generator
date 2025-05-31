package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.Verb;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewWordStrategyTest {

    private static NewWordStrategy strategy;

    @BeforeAll
    static void setUpClass() {
        strategy = new NewWordStrategy();
    }

    @AfterAll
    static void tearDownClass() {
        strategy = null;
    }

    /**
     * Test to verify nouns are correctly populated based on structure placeholders.
     */
    @Test
    void testPopulateWordsPopulatesNouns() {
        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This is a [noun] and another [noun]."));

        strategy.populateWords(sentence);

        int expectedNounsCount = 2;
        List<String> nouns = sentence.getNouns();
        assertEquals(expectedNounsCount, nouns.size(), "Noun list does not contain the expected number of words.");
        assertTrue(nouns.stream().allMatch(n -> n != null && !n.isEmpty()), "Noun list contains invalid words.");
    }

    /**
     * Test to verify verbs are correctly populated based on structure placeholders.
     */
    @Test
    void testPopulateWordsPopulatesVerbs() {
        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This [verb] and that [verb]."));
        Verb.getInstance().configureVerbTense(false);

        strategy.populateWords(sentence);

        int expectedVerbsCount = 2;
        List<String> verbs = sentence.getVerbs();
        assertEquals(expectedVerbsCount, verbs.size(), "Verb list does not contain the expected number of words.");
        assertTrue(verbs.stream().allMatch(v -> v != null && !v.isEmpty()), "Verb list contains invalid words.");
    }

    /**
     * Test to verify adjectives are correctly populated based on structure placeholders.
     */
    @Test
    void testPopulateWordsPopulatesAdjectives() {
        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("A [adjective] day and a [adjective] night."));

        strategy.populateWords(sentence);

        int expectedAdjectivesCount = 2;
        List<String> adjectives = sentence.getAdjectives();
        assertEquals(expectedAdjectivesCount, adjectives.size(), "Adjective list does not contain the expected number of words.");
        assertTrue(adjectives.stream().allMatch(a -> a != null && !a.isEmpty()), "Adjective list contains invalid words.");
    }

    /**
     * Test to verify no words are added if placeholders do not exist in the structure.
     */
    @Test
    void testPopulateWordsWithNoPlaceholders() {
        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This sentence has no placeholders."));

        strategy.populateWords(sentence);

        assertTrue(sentence.getNouns().isEmpty(), "Noun list should be empty when no noun placeholders exist.");
        assertTrue(sentence.getVerbs().isEmpty(), "Verb list should be empty when no verb placeholders exist.");
        assertTrue(sentence.getAdjectives().isEmpty(), "Adjective list should be empty when no adjective placeholders exist.");
    }

    /**
     * Test to verify words are not duplicated when existing lists contain enough words.
     */
    @Test
    void testPopulateWordsDoesNotDuplicateExistingWords() {
        List<String> predefinedNouns = new ArrayList<>();
        predefinedNouns.add("cat");
        predefinedNouns.add("dog");

        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This is a [noun] and another [noun]."));
        sentence.setNouns(predefinedNouns);

        strategy.populateWords(sentence);

        List<String> nouns = sentence.getNouns();
        assertEquals(predefinedNouns.size(), nouns.size(), "Noun list size should not change if enough words exist.");
        assertTrue(nouns.containsAll(predefinedNouns), "Existing nouns should not be modified.");
    }

    /**
     * Test to verify words are partially populated when some are already in the lists.
     */
    @Test
    void testPopulateWordsPartiallyPopulatesLists() {
        List<String> predefinedNouns = new ArrayList<>();
        predefinedNouns.add("cat");

        Sentence sentence = new Sentence();
        sentence.setStructure(new StringBuilder("This is a [noun] and another [noun]."));
        sentence.setNouns(predefinedNouns);

        strategy.populateWords(sentence);

        assertEquals(2, sentence.getNouns().size(), "Noun list should contain two words after partial population.");
    }
}