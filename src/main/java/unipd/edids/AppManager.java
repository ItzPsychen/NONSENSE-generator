//Facade

package unipd.edids;


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

    public Sentence generateSentence(){
        if (inputSentence == null) {
            return null;
        }
        outputSentence = generateSentenceService.generateSentence(inputSentence);
        return outputSentence;
    }

}
