// Facade

package unipd.edids.logicBusiness;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.managers.ConfigManager;
import unipd.edids.logicBusiness.managers.FileManager;
import unipd.edids.logicBusiness.services.AnalyzeSentenceService;
import unipd.edids.logicBusiness.services.GenerateSentenceService;
import unipd.edids.logicBusiness.services.ModerationSentenceService;
import unipd.edids.logicBusiness.strategies.structureStrategies.RandomStructureStrategy;
import unipd.edids.logicBusiness.strategies.structureStrategies.SameAsAnalyzedStructureStrategy;
import unipd.edids.logicBusiness.strategies.structureStrategies.SelectedStructureStrategy;
import unipd.edids.logicBusiness.strategies.tenseStrategies.FutureTenseStrategy;
import unipd.edids.logicBusiness.strategies.tenseStrategies.PresentTenseStrategy;
import unipd.edids.logicBusiness.strategies.wordSelectionStrategies.NewWordStrategy;
import unipd.edids.logicBusiness.strategies.wordSelectionStrategies.OriginalWordStrategy;


//FIX il facade chiama il service layer, non ha logica di business
// inoltre ha come istanza ConfigManager, LoggerManager e FileManager

public class AppManager {
    private static final Logger logger = LogManager.getLogger(AppManager.class);

    private Sentence inputSentence;
    private Sentence outputSentence;
    private AnalyzeSentenceService analyzeSentenceService;
    private GenerateSentenceService generateSentenceService;
    private ModerationSentenceService moderationSentenceService;
    private boolean modified;
    private ConfigManager configManager;

    public AppManager() {
        analyzeSentenceService = new AnalyzeSentenceService();
        generateSentenceService = new GenerateSentenceService();
        moderationSentenceService = new ModerationSentenceService();
        configManager = ConfigManager.getInstance();
        modified = true;
    }

    public Sentence analyzeSentence(String text, boolean saveSelected) {
        validateText(text);
        inputSentence = analyzeSentenceService.analyzeSyntax(text);
        if (saveSelected)
            FileManager.appendLineToSavingFile(configManager.getProperty("analyzed.save.file"), inputSentence.toString());
        return inputSentence;
    }

    public Sentence generateSentence(String strategy, String selStructure, boolean toxicity, boolean futureTense, boolean newWords, boolean saveSelected) {


        if (inputSentence == null) {
            if (!newWords || (strategy.equals("SAME")))
                throw new IllegalArgumentException("Input sentence cannot be null. Please analyze a sentence first or enable the 'new words' option while selecting a valid structure (Random or Selected).");
        }


        switch (strategy) {
            case "RANDOM":
                generateSentenceService.setStructureSentenceStrategy(new RandomStructureStrategy());
                break;
            case "SAME":
                generateSentenceService.setStructureSentenceStrategy(new SameAsAnalyzedStructureStrategy(inputSentence));
                break;
            case "SELECTED":
                generateSentenceService.setStructureSentenceStrategy(new SelectedStructureStrategy(selStructure));
                break;
            default:
                logger.error("Unknown strategy: {}", strategy);
                throw new IllegalArgumentException("Invalid strategy: " + strategy);
        }

        if (futureTense) {
            Verb.getInstance().setTenseStrategy(new FutureTenseStrategy());
        } else {
            Verb.getInstance().setTenseStrategy(new PresentTenseStrategy());
        }

        if (newWords) {
            generateSentenceService.setWordsStrategi(new NewWordStrategy());
        } else {
            generateSentenceService.setWordsStrategi(new OriginalWordStrategy(inputSentence));
        }

        outputSentence = generateSentenceService.generateSentence();
        if (toxicity) {
            moderationSentenceService.moderateText(outputSentence);
        }

        if (saveSelected) {
            FileManager.appendLineToSavingFile(configManager.getProperty("generated.save.file"), outputSentence.toString());

        }
        return outputSentence;
    }



    public void clearAll() {
        this.inputSentence = null;
        this.outputSentence = null;
    }


    public Sentence getInputSentence() {
        return inputSentence;
    }

    public Sentence getOutputSentence() {
        return outputSentence;
    }

    private void validateText(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be null or empty.");
        }
        int maxLength = Integer.parseInt(configManager.getProperty("max.sentence.length"));
        if (text.length() > maxLength) {
            throw new IllegalArgumentException("Input text cannot exceed " + maxLength + " characters.");
        }
        if (text.trim().isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be empty or whitespace only.");
        }
        if (!text.matches(".*[a-zA-Z]+.*")) {
            throw new IllegalArgumentException("Input text must contain at least one alphabetical character.");
        }


        if (text.matches("^[^a-zA-Z0-9\\s].*")) {
            throw new IllegalArgumentException("Input text contains invalid characters at the start of the text.");
        }
        if (text.matches(".*[^a-zA-Z0-9.\\s]$")) {
            throw new IllegalArgumentException("Input text contains invalid characters at the end of the text.");
        }
        if (text.matches(".*[^a-zA-Z0-9.,:'\"\\s].*")) {
            throw new IllegalArgumentException("Input text contains invalid characters.");
        }


    }
}
