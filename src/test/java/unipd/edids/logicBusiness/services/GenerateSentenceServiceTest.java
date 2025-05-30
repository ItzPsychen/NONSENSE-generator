package unipd.edids.logicBusiness.services;

import org.junit.jupiter.api.*;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.strategies.structureStrategies.StrategyType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenerateSentenceServiceTest {

    private ConfigManager configManager;

    @BeforeEach
    public void setUp() {
        configManager = ConfigManager.getInstance();
        Verb.getInstance().configureVerbTense(false);


    }

    @AfterEach
    public void tearDown() throws IOException {
        configManager.resetDefault(configManager.getProperty("api.key.file"));
        File file = new File("testFile.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testGenerateSentenceWithRandomStructureStrategyAndNewWordStrategy() {
        GenerateSentenceService service = new GenerateSentenceService();
        service.setStructureSentenceStrategy(StrategyType.RANDOM, null, null);
        service.configureWordStrategy(true, null);

        Sentence result = service.generateSentence();

        assertNotNull(result.getSentence().toString(), "Generated sentence should not be null.");
        assertTrue(result.getSentence().length() > 0, "Generated sentence should not be empty.");
    }

    @Test
    void testGenerateSentenceWithSelectedStructureStrategy() {
        GenerateSentenceService service = new GenerateSentenceService();
        String selectedStructure = "[noun] [verb] [adjective]";
        service.setStructureSentenceStrategy(StrategyType.SELECTED, null, selectedStructure);
        service.configureWordStrategy(true, null);

        Sentence result = service.generateSentence();

        assertNotNull(result.getSentence().toString(), "Generated sentence should not be null.");
        assertFalse(result.getSentence().isEmpty(), "Generated sentence should not be empty.");
    }

    @Test
    void testGenerateSentenceWithOriginalWordStrategy() {
        GenerateSentenceService service = new GenerateSentenceService();
        Sentence inputSentence = new Sentence();
        inputSentence.setNouns(Arrays.asList("cat", "dog"));
        inputSentence.setVerbs(Arrays.asList("runs", "jumps"));
        inputSentence.setAdjectives(Arrays.asList("fast", "playful"));

        service.setStructureSentenceStrategy(StrategyType.RANDOM, inputSentence, null);
        service.configureWordStrategy(false, inputSentence);

        Sentence result = service.generateSentence();

        assertNotNull(result.getSentence().toString(), "Generated sentence should not be null.");
        assertFalse(result.getSentence().isEmpty(), "Generated sentence should not be empty.");
    }

    @Test
    void testGenerateSentenceWithRecursiveStructureEnabled() {
        GenerateSentenceService service = new GenerateSentenceService();
        service.setStructureSentenceStrategy(StrategyType.RANDOM, null, null);

        Sentence inputSentence = new Sentence();
        inputSentence.setStructure(new StringBuilder("[sentence] [noun]"));
        service.configureWordStrategy(true, inputSentence);

        Sentence result = service.generateSentence();

        assertNotNull(result.getSentence().toString(), "Generated sentence should not be null.");
        assertFalse(result.getSentence().isEmpty(), "Generated sentence should not be empty.");
    }

    @Test
    void testRecursiveStructureExceedsMaxRecursionDepth() {
        ConfigManager.getInstance().setProperty("allow.recursive.sentences", "true");
        ConfigManager.getInstance().setProperty("max.recursion.level", "1");

        GenerateSentenceService service = new GenerateSentenceService();
        Sentence inputSentence = new Sentence();
        inputSentence.setStructure(new StringBuilder("[sentence] [sentence]"));

        service.setStructureSentenceStrategy(StrategyType.RANDOM, null, null);
        service.configureWordStrategy(true, inputSentence);

        Sentence result = service.generateSentence();

        assertNotNull(result.getSentence(), "Generated sentence should not be null even with excess recursion depth.");
    }





    @Test
    void testSetStructureSentenceStrategyThrowsExceptionForInvalidStrategy() {
        GenerateSentenceService service = new GenerateSentenceService();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.setStructureSentenceStrategy(null, null, null)
        );

        assertEquals("Strategy cannot be null.", exception.getMessage(),
                "Exception message should match for invalid strategy.");
    }

    @Test
    void testValidateInputThrowsExceptionWhenInputSentenceIsNull() {
        GenerateSentenceService service = new GenerateSentenceService();

        Exception exceptionSame = assertThrows(IllegalArgumentException.class, () ->
                service.validateInput(null, false, StrategyType.SAME),
                "Expected IllegalArgumentException when inputSentence is null, newWords is false, and strategy is SAME."
        );
        assertEquals("Input sentence cannot be null. Please analyze a sentence first or enable the 'new words' option while selecting a valid structure (Random or Selected).",
                exceptionSame.getMessage(), "Exception message should match for null input sentence, SAME strategy.");

        Exception exceptionRandom = assertThrows(IllegalArgumentException.class, () ->
                service.validateInput(null, false, StrategyType.RANDOM),
                "Expected IllegalArgumentException when inputSentence is null, newWords is false, and strategy is RANDOM."
        );
        assertEquals("Input sentence cannot be null. Please analyze a sentence first or enable the 'new words' option while selecting a valid structure (Random or Selected).",
                exceptionRandom.getMessage(), "Exception message should match for null input sentence, RANDOM strategy.");

        Exception exceptionSelected = assertThrows(IllegalArgumentException.class, () ->
                service.validateInput(null, false, StrategyType.SELECTED),
                "Expected IllegalArgumentException when inputSentence is null, newWords is false, and strategy is SELECTED."
        );
        assertEquals("Input sentence cannot be null. Please analyze a sentence first or enable the 'new words' option while selecting a valid structure (Random or Selected).",
                exceptionSelected.getMessage(), "Exception message should match for null input sentence, SELECTED strategy.");
    }

    @Test
    void testValidateInputDoesNotThrowExceptionWhenInputSentenceIsNotNull() {
        GenerateSentenceService service = new GenerateSentenceService();
        Sentence validSentence = new Sentence();
        validSentence.setStructure(new StringBuilder("[noun] [verb] [adjective]"));

        assertDoesNotThrow(() -> service.validateInput(validSentence, false, StrategyType.SAME),
                "validateInput should not throw an exception when inputSentence is valid, newWords is false, and strategy is SAME.");

        assertDoesNotThrow(() -> service.validateInput(validSentence, false, StrategyType.RANDOM),
                "validateInput should not throw an exception when inputSentence is valid, newWords is false, and strategy is RANDOM.");

        assertDoesNotThrow(() -> service.validateInput(validSentence, false, StrategyType.SELECTED),
                "validateInput should not throw an exception when inputSentence is valid, newWords is false, and strategy is SELECTED.");
    }

    @Test
    void testValidateInputDoesNotThrowExceptionWhenNewWordsIsTrue() {
        GenerateSentenceService service = new GenerateSentenceService();

        assertDoesNotThrow(() -> service.validateInput(null, true, StrategyType.RANDOM),
                "validateInput should not throw an exception when inputSentence is null, newWords is true, and strategy is RANDOM.");

        assertDoesNotThrow(() -> service.validateInput(null, true, StrategyType.SELECTED),
                "validateInput should not throw an exception when inputSentence is null, newWords is true, and strategy is SELECTED.");
    }

    @Test
    void testReplacePlaceholders() {
        GenerateSentenceService service = new GenerateSentenceService();
        Sentence inputSentence = new Sentence();
        inputSentence.setStructure(new StringBuilder("[noun] [verb] [adjective]"));
        inputSentence.setNouns(List.of("cat"));
        inputSentence.setVerbs(List.of("jumps"));
        inputSentence.setAdjectives(List.of("playful"));

        service.configureWordStrategy(false, inputSentence);
        service.setStructureSentenceStrategy(StrategyType.SAME, inputSentence, null);
        Sentence result = service.generateSentence();

        assertEquals("Cat jumps playful", result.getSentence().toString(),
                "The sentence should correctly replace placeholders with provided words.");
    }


    @Test
    void testGeneratingSentenceWithRecursiveStructureAndMaxDepth() {
        ConfigManager.getInstance().setProperty("allow.recursive.sentences", "true");
        ConfigManager.getInstance().setProperty("max.recursion.level", "2");

        GenerateSentenceService service = new GenerateSentenceService();
        Sentence inputSentence = new Sentence();
        inputSentence.setStructure(new StringBuilder("[sentence] [verb] [adjective]"));

        service.setStructureSentenceStrategy(StrategyType.SAME, inputSentence, null);
        service.configureWordStrategy(true, inputSentence);

        Sentence result = service.generateSentence();

        assertNotNull(result.getSentence().toString(), "Generated sentence should not be null.");
        assertFalse(result.getSentence().isEmpty(), "Generated sentence should not be empty.");
        assertFalse(result.getSentence().toString().contains("[sentence]"), "Generation should resolve all recursive tags.");
    }

    @Test
    void testGenerateSentenceWithWordStrategyConfigurations() {
        GenerateSentenceService service = new GenerateSentenceService();
        Sentence inputSentence = new Sentence();
        inputSentence.setNouns(List.of("rabbit"));
        inputSentence.setVerbs(List.of("hops"));
        inputSentence.setAdjectives(List.of("quickly"));
        inputSentence.setStructure(new StringBuilder("[noun] [verb] [adjective]"));

        service.setStructureSentenceStrategy(StrategyType.SAME, inputSentence, null);
        service.configureWordStrategy(false, inputSentence);

        Sentence originalWordResult = service.generateSentence();


        assertEquals("Rabbit hops quickly", originalWordResult.getSentence().toString(),
                "Generated sentence should adhere to the OriginalWordStrategy.");

        service.configureWordStrategy(true, null);

        Sentence newWordResult = service.generateSentence();

        assertNotNull(newWordResult.getSentence().toString(), "Generated sentence (NewWordStrategy) should not be null.");
        assertFalse(newWordResult.getSentence().isEmpty(), "Generated sentence (NewWordStrategy) should not be empty.");
    }

    @Test
    void testCapitalizationInGeneratedSentence() {
        GenerateSentenceService service = new GenerateSentenceService();
        Sentence inputSentence = new Sentence();
        inputSentence.setStructure(new StringBuilder("[noun] [verb]."));
        inputSentence.setNouns(List.of("dog"));
        inputSentence.setVerbs(List.of("ran"));

        service.setStructureSentenceStrategy(StrategyType.SAME, inputSentence, null);
        service.configureWordStrategy(false, inputSentence);

        Sentence result = service.generateSentence();

        assertEquals("Dog ran.", result.getSentence().toString(),
                "Generated sentence should start with a capitalized first letter.");
    }

    @Test
    void testGenerateSentenceWithEmptyTemplate() {
        GenerateSentenceService service = new GenerateSentenceService();
        Sentence inputSentence = new Sentence();
        inputSentence.setStructure(new StringBuilder(""));

        service.setStructureSentenceStrategy(StrategyType.SAME, inputSentence, null);
        service.configureWordStrategy(true, inputSentence);

        Sentence result = service.generateSentence();

        assertNotNull(result.getSentence(), "Generated sentence should not be null.");
        assertEquals("", result.getSentence().toString(), "Generated sentence should be empty when the template is empty.");
    }
}