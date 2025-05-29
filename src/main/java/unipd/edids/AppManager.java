// Facade

package unipd.edids;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.entities.Verb;
import unipd.edids.strategies.*;

import java.io.FileWriter;
import java.io.IOException;


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

    public Sentence analyzeSentence(String text, boolean saveSelected)  {
        ///////TODO lanciare eccezioni
//        if (!this.isModified()) return new Sentence("#notmodified");
//        if (text == null || text.trim().isEmpty()) return new Sentence("#length");
//        if (!text.matches(".*[a-zA-Z]+.*")) return new Sentence("#chars");

        inputSentence = analyzeSentenceService.analyzeSyntax(text);
//        moderationSentenceService.moderateText(inputSentence);
//        if (!inputSentence.isValid()) return new Sentence("#invalid");;

        if (saveSelected) {

                FileManager.appendLineToSavingFile(configManager.getProperty("analyzed.save.file"), inputSentence.toString());

        }
        return inputSentence;
    }

    public Sentence generateSentence(String strategy, String selStructure, boolean toxicity, boolean futureTense, boolean newWords, boolean saveSelected) {

        if (inputSentence == null) {
            return null;
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

        outputSentence = generateSentenceService.generateSentence(inputSentence);
        if (toxicity) {
            moderationSentenceService.moderateText(outputSentence);
        }
// TODO
// analyzeSentenceService.setValidateAttributes(outputSentence);
//        if (outputSentence == null || !outputSentence.isValid()) return null;
//        if (save && outputSentence != null) saveSentence(outputSentence);


        if (saveSelected) {
                FileManager.appendLineToSavingFile(configManager.getProperty("generated.save.file"), outputSentence.toString());

        }
        return outputSentence;
    }



    public boolean isModified() {
        return this.modified;
    }

    public void setModified(boolean value) {
        this.modified = value;
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

    public GenerateSentenceService getGenerateSentenceService() {
        return generateSentenceService;
    }
}
