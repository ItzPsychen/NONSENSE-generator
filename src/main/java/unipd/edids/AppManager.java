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
    public Sentence analyzeSentence(String text){
        inputSentence = analyzeSentenceService.analyzeSyntax(text);
        return inputSentence;
    }

    public Sentence generateSentence(boolean save){
        if (inputSentence == null) {
            return null;
        }
        outputSentence = generateSentenceService.generateSentence(inputSentence);
        if(save) saveSentence(outputSentence);
        return outputSentence;
    }
    private void saveSentence(Sentence outputSentence){

        String nomeFile = "src/main/resources/output.txt";  // Nome del file

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeFile, true))) {
            writer.write(outputSentence.getSentence().toString()+System.lineSeparator());
            System.out.println("Testo salvato con successo in " + nomeFile);
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file: " + e.getMessage());
        }
    }

}
