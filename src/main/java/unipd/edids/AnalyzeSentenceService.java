package unipd.edids;

import com.google.cloud.language.v1.*;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONObject;

//Fix chiamare API bene

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
            String pos = String.valueOf(token.getPartOfSpeech().getTag());
            switch (pos) {
                case "NOUN":
                    analyzedSentence.getNouns().add(word);
                    analyzedSentence.getStructure().append("[noun] ");
                    break;
                case "VERB":
                    analyzedSentence.getVerbs().add(word);
                    analyzedSentence.getStructure().append("[verb] ");
                    break;
                case "ADJ":
                    analyzedSentence.getAdjectives().add(word);
                    analyzedSentence.getStructure().append("[adjective] ");
                    break;
                default:
                    analyzedSentence.getStructure().append(word).append(" ");
            }
        }
        analyzedSentence.setStructure(new StringBuilder(analyzedSentence.getStructure().toString().trim()));
        analyzedSentence.setSyntaxTree(getSyntaxTree(text));

        System.out.println(analyzedSentence.getStructure());
        return analyzedSentence;
    }


    private StanfordCoreNLP pipeline;

    public AnalyzeSentenceService() {
        // Configura le proprietà per la pipeline NLP
        Properties props = new Properties();
        // Aggiungi gli annotatori necessari per il parsing
        // "tokenize" per dividere la frase in parole
        // "ssplit" per dividere il testo in frasi
        // "pos" per il Part-of-Speech tagging
        // "parse" per il parsing sintattico (genera il syntax tree)
        props.setProperty("annotators", "tokenize,ssplit,pos,parse");

        // Inizializza la pipeline di StanfordCoreNLP
        // Questo carica i modelli, può richiedere un po' di tempo la prima volta
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


    /**
     * Metodo di esempio per visualizzare l'albero.
     *
     * @param tree L'albero sintattico da stampare.
     */
    public void printTree(Tree tree) {
        if (tree != null) {
            tree.pennPrint(); // Stampa l'albero in formato Penn Treebank
        } else {
            System.out.println("Nessun albero da stampare.");
        }
    }
}