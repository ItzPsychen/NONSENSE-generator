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

public class AnalyzeSentenceService {
    private static final Logger logger = LogManager.getLogger(AnalyzeSentenceService.class);

    /**
     * Esegue l'analisi della sintassi di un testo.
     *
     * @param text il testo da analizzare.
     * @return una lista di tokens che rappresentano l'analisi della sintassi.
     */

    public Sentence analyzeSyntax(String text) {

        Sentence temp = new Sentence();
        temp.setSentence(new StringBuilder(text));
        List<Token> tokens = fetchSyntaxTokens(text);
        temp.setStructure(new StringBuilder());
        for (Token token : tokens) {
            String word = token.getText().getContent();
            String pos = String.valueOf(token.getPartOfSpeech().getTag());
//            temp.getSyntaxTree().append("Lemma: ").append(word).append(", Part of speach: ").append(pos).append("\n");
            switch (pos) {
                case "NOUN":
                    temp.getNouns().add(word);
                    temp.getStructure().append("[noun] ");
                    break;
                case "VERB":
                    temp.getVerbs().add(word);
                    temp.getStructure().append("[verb] ");
                    break;
                case "ADJ":
                    temp.getAdjectives().add(word);
                    temp.getStructure().append("[adjective] ");
                    break;
                default:
                    temp.getStructure().append(word).append(" ");
            }
        }
        temp.setStructure(new StringBuilder(temp.getStructure().toString().trim()));

        temp.setSyntaxTree(getSyntaxTree(text));

        // imposta la tossicita', profanita' ...
        setValidateAttributes(temp);

        System.out.println(temp.getStructure());
        return temp;
    }

    public List<Token> fetchSyntaxTokens(String text) {
        LanguageServiceClient language = APIClient.getInstance(); // Ottieni il client singleton

        try {
            // Creazione del documento da analizzare
            Document doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

            // Creazione della richiesta per l'analisi della sintassi
            AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF16) // Specifica la codifica del testo
                    .build();

            // Esegui l'analisi
            AnalyzeSyntaxResponse response = language.analyzeSyntax(request);

            // Restituisci i token analizzati
            return response.getTokensList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setValidateAttributes(Sentence temp) {

        // return;

        try {
            // PERSPECTIVE_KEY here
            String apiKey = "####################################################################" +
                            "####################################################################" +
                            "####################################################################" +
                            "####################################################################";

            URL url = new URL("https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject requestBody = new JSONObject()
                    .put("comment", new JSONObject().put("text", temp.getSentence()))
                    .put("languages", new org.json.JSONArray().put("en"))
                    .put("requestedAttributes", new JSONObject()
                            .put("TOXICITY", new JSONObject())
                            .put("PROFANITY", new JSONObject())
                            .put("INSULT", new JSONObject())
                            .put("THREAT", new JSONObject())
                            .put("IDENTITY_ATTACK", new JSONObject()));

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject JSONattributes = jsonResponse.getJSONObject("attributeScores");

                temp.setToxicity(JSONattributes.getJSONObject("TOXICITY").getJSONObject("summaryScore").getDouble("value"));
                temp.setProfanity(JSONattributes.getJSONObject("PROFANITY").getJSONObject("summaryScore").getDouble("value"));
                temp.setInsult(JSONattributes.getJSONObject("INSULT").getJSONObject("summaryScore").getDouble("value"));
                temp.setThreat(JSONattributes.getJSONObject("THREAT").getJSONObject("summaryScore").getDouble("value"));
                temp.setIdentityThreat(JSONattributes.getJSONObject("IDENTITY_THREAT").getJSONObject("summaryScore").getDouble("value"));
            }

        } catch (Exception e) {
            logger.error("Errore durante il calcolo del punteggio di tossicità", e);
        }
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