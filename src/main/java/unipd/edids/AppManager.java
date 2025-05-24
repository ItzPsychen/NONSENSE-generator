// Facade

package unipd.edids;

import java.io.FileWriter;
import java.io.IOException;

public class AppManager {
    private Sentence inputSentence;
    private Sentence outputSentence;
    private AnalyzeSentenceService analyzeSentenceService;
    private GenerateSentenceService generateSentenceService;
    private ModerationSentenceService moderationSentenceService;
    private boolean modified;

    public AppManager(){
        analyzeSentenceService = new AnalyzeSentenceService();
        generateSentenceService = new GenerateSentenceService();
        moderationSentenceService = new ModerationSentenceService();
        modified = true;
    }

    public Sentence analyzeSentence(String text){
        if (!this.isModified()) return new Sentence("#notmodified");
        if (text == null || text.trim().isEmpty()) return new Sentence("#length");
        if (!text.matches(".*[a-zA-Z]+.*")) return new Sentence("#chars");

        inputSentence = analyzeSentenceService.analyzeSyntax(text);
        moderationSentenceService.moderateText(inputSentence);
        if (!inputSentence.isValid()) return new Sentence("#invalid");;

        System.out.println("BOMBA " + inputSentence.getSentence().toString());
        return inputSentence;
    }

    public Sentence generateSentence(boolean save){
        if (inputSentence == null) {
            return null;
        }
        outputSentence = generateSentenceService.generateSentence(inputSentence);
        // analyzeSentenceService.setValidateAttributes(outputSentence);
        if (outputSentence == null || !outputSentence.isValid()) return null;
        if (save && outputSentence != null) saveSentence(outputSentence);
        return outputSentence;
    }

    // Agginge al file ./logs/output/generated.txt
    private void saveSentence(Sentence generated){
        String generatedPath = ConfigManager.getInstance().getProperty("generated.nonsense", "./logs/output/generated.txt");
        try (FileWriter writer = new FileWriter(generatedPath, true)) {
            writer.write(generated.getSentence() + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Agginge al file ./logs/output/details.txt
    private void saveAnalysis(String text, String analysis){
        String detailsPath = ConfigManager.getInstance().getProperty("details.nonsense", "./logs/output/details.txt");
        try (FileWriter writer = new FileWriter(detailsPath, true)) {
            writer.write(text + System.lineSeparator() + analysis + System.lineSeparator());
            if (!analysis.isEmpty()) writer.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isModified() { return this.modified; }
    public void setModified(boolean value) { this.modified = value; }
}
