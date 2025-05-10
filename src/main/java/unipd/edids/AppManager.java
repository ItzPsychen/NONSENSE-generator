//Facade

package unipd.edids;



public class AppManager {
    private Sentence inputSentence;
    private Sentence outputSentence;
    private AnalyzeSentenceService analyzeSentenceService;

    public AppManager(){
     analyzeSentenceService = new AnalyzeSentenceService();
    }
    public Sentence analyzeSentence(String text){
        return analyzeSentenceService.analyzeSyntax(text);
    }

}
