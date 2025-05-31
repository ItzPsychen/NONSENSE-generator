package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.strategies.tenseStrategies.FutureTenseStrategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OriginalWordStrategyTest {

    @BeforeAll
    static void initAll() {
        System.out.println("Starting tests for OriginalWordStrategy...");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("Finished tests for OriginalWordStrategy.");
    }

    @Test
    void testPopulateWordsCopiesInputSentenceWords() {
        Sentence inputSentence = new Sentence();
        inputSentence.setNouns(List.of("dog", "cat"));
        inputSentence.setVerbs(List.of("run", "jump"));
        inputSentence.setAdjectives(List.of("quick", "lazy"));

        Sentence tempSentence = new Sentence();
        OriginalWordStrategy strategy = new OriginalWordStrategy(inputSentence);

        strategy.populateWords(tempSentence);
        assertTrue(inputSentence.getNouns().containsAll(tempSentence.getNouns()), "Nouns were not copied correctly.");
        assertTrue(inputSentence.getVerbs().containsAll(tempSentence.getVerbs()), "Verbs were not copied correctly.");
        assertTrue(inputSentence.getAdjectives().containsAll(tempSentence.getAdjectives()), "Adjectives were not copied correctly.");
         }

    @Test
    void testPopulateWordsFillsPlaceholdersInStructure() {
        Sentence inputSentence = new Sentence();
        inputSentence.setNouns(List.of());
        inputSentence.setVerbs(List.of());
        inputSentence.setAdjectives(List.of());

        Sentence tempSentence = new Sentence();
        tempSentence.setStructure(new StringBuilder("[noun] [verb] [noun] [adjective]"));
        OriginalWordStrategy strategy = new OriginalWordStrategy(inputSentence);
        Verb.getInstance().configureVerbTense(false);
        strategy.populateWords(tempSentence);

        assertEquals(2, tempSentence.getNouns().size(), "Nouns placeholder(s) were not filled correctly.");
        assertEquals(1, tempSentence.getVerbs().size(), "Verb placeholder(s) were not filled correctly.");
        assertEquals(1, tempSentence.getAdjectives().size(), "Adjective placeholder(s) were not filled correctly.");
    }

    @Test
    void testPopulateWordsConjugatesFutureTenseVerbs() {
        Sentence inputSentence = new Sentence();
        inputSentence.setVerbs(List.of("go", "come"));

        // Simulating FutureTenseStrategy applied
        FutureTenseStrategy futureTenseStrategy = new FutureTenseStrategy();
        Verb.getInstance().configureVerbTense(true);

        Sentence tempSentence = new Sentence();
        OriginalWordStrategy strategy = new OriginalWordStrategy(inputSentence);

        strategy.populateWords(tempSentence);

        tempSentence.getVerbs().forEach(verb -> assertNotNull(futureTenseStrategy.conjugate(verb)));
    }

    @Test
    void testPopulateWordsShufflesWordLists() {
        Sentence inputSentence = new Sentence();
        inputSentence.setNouns(List.of("apple", "banana", "cherry"));
        inputSentence.setVerbs(List.of("eat", "drink", "consume"));
        inputSentence.setAdjectives(List.of("fresh", "ripe", "juicy"));

        Sentence tempSentence = new Sentence();
        OriginalWordStrategy strategy = new OriginalWordStrategy(inputSentence);

        strategy.populateWords(tempSentence);

        assertEquals(3, tempSentence.getNouns().size(), "Noun list size should not change after shuffle.");
        assertEquals(3, tempSentence.getVerbs().size(), "Verb list size should not change after shuffle.");
        assertEquals(3, tempSentence.getAdjectives().size(), "Adjective list size should not change after shuffle.");
    }
}