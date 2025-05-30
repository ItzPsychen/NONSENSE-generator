package unipd.edids.logicBusiness.services;

import com.google.cloud.language.v1.*;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;

import java.util.List;

import java.util.Properties;

public class AnalyzeSentenceService {
    private static final Logger logger = LogManager.getLogger(AnalyzeSentenceService.class);

    /**
     * Esegue l'analisi della sintassi di un testo.
     *
     * @param text il testo da analizzare.
     * @return una lista di tokens che rappresentano l'analisi della sintassi.
     */

    public Sentence analyzeSyntax(String text) {
        Sentence analyzedSentence = new Sentence(text);
        List<Token> tokens =  new APIClient<AnalyzeSyntaxResponse>()
                .setSentenceToAPI(text)
                .setAPIType(APIClient.RequestType.SYNTAX)
                .execute().getTokensList();
        for (Token token : tokens) {
            String word = token.getText().getContent();
            PartOfSpeech.Tag pos = token.getPartOfSpeech().getTag();
            switch (pos) {
                case NOUN:
                    analyzedSentence.getNouns().add(word);
                    analyzedSentence.getStructure().append("[noun] ");
                    break;
                case VERB:
                    analyzedSentence.getVerbs().add(word);
                    analyzedSentence.getStructure().append("[verb] ");
                    break;
                case ADJ:
                    analyzedSentence.getAdjectives().add(word);
                    analyzedSentence.getStructure().append("[adjective] ");
                    break;
                case X:
                case UNKNOWN:
                    throw new IllegalArgumentException("Invalid token for grammatical type: " + pos + ", word: " + word);
                default:
                    analyzedSentence.getStructure().append(word).append(" ");
            }
        }
        analyzedSentence.setStructure(new StringBuilder(analyzedSentence.getStructure().toString().trim()));
        analyzedSentence.setSyntaxTree(getSyntaxTree(text));

        System.out.println(analyzedSentence.getStructure());
        return analyzedSentence;
    }


    private final StanfordCoreNLP pipeline;

    public AnalyzeSentenceService() {
        Properties props = new Properties();

        props.setProperty("annotators", "tokenize,ssplit,pos,parse");

        pipeline = new StanfordCoreNLP(props);
    }

    /**
     * Genera e ritorna l'albero sintattico per la prima frase di un testo.
     *
     * @param text La frase o il testo in inglese da analizzare.
     * @return L'oggetto Tree che rappresenta l'albero sintattico, o null se non ci sono frasi.
     */
    public Tree getSyntaxTree(String text) {
        // Crea un CoreDocument dall'input text
        CoreDocument document = pipeline.processToCoreDocument(text);

        // Stanford CoreNLP può processare documenti con più frasi.
        // Qui prendiamo solo la prima frase per semplicità.
        if (!document.sentences().isEmpty()) {
            CoreSentence sentence = document.sentences().get(0);
            return sentence.constituencyParse(); // Questo è l'albero sintattico
        } else {
            return null;
        }
    }

}