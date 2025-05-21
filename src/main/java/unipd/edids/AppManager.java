//Facade

package unipd.edids;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AppManager {
    private Sentence inputSentence;
    private Sentence outputSentence;
    private AnalyzeSentenceService analyzeSentenceService;
    private GenerateSentenceService generateSentenceService;

    public AppManager(){
        analyzeSentenceService = new AnalyzeSentenceService();
        generateSentenceService = new GenerateSentenceService();
    }

    public Sentence analyzeSentence(String text, boolean save){
        inputSentence = analyzeSentenceService.analyzeSyntax(text);
        if (save) saveAnalysis(outputSentence, inputSentence.toString());
        return inputSentence;
    }

    public Sentence generateSentence(boolean save){
        if (inputSentence == null) {
            return null;
        }
        outputSentence = generateSentenceService.generateSentence(inputSentence);
        if (save) saveSentence(outputSentence);
        return outputSentence;
    }

    // Agginge al file ./logs/output/generated.txt
    private void saveSentence(Sentence generated){
        String generatedPath = ConfigManager.getInstance().getProperty("GENERATED_NONSENSE", "./logs/output/generated.txt");
        try (FileWriter writer = new FileWriter(generatedPath, true)) {
            writer.write(generated.getSentence() + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Agginge al file ./logs/output/details.txt
    private void saveAnalysis(Sentence generated, String analysis){
        String detailsPath = ConfigManager.getInstance().getProperty("DETAILS_NONSENSE", "./logs/output/details.txt");
        try (FileWriter writer = new FileWriter(detailsPath, true)) {
            writer.write(generated.getSentence() + System.lineSeparator() + analysis + System.lineSeparator());
            if (!analysis.isEmpty()) writer.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
