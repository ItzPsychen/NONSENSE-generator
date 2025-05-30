package unipd.edids.logicBusiness.services;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.PartOfSpeech;
import com.google.cloud.language.v1.Token;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.managers.ConfigManager;

import java.util.List;
import java.util.Properties;

public class AnalyzeSentenceService {
    private static final Logger logger = LogManager.getLogger(AnalyzeSentenceService.class);

    private static final String NOUN_TAG = "[noun] ";
    private static final String VERB_TAG = "[verb] ";
    private static final String ADJECTIVE_TAG = "[adjective] ";
    private static final String ERROR_INVALID_TOKEN = "Invalid token for grammatical type";

    private final StanfordCoreNLP pipeline;

    public AnalyzeSentenceService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse");
        this.pipeline = new StanfordCoreNLP(props);
        logger.info("StanfordCoreNLP pipeline initialized with properties: {}", props);
    }

    public Sentence analyzeSyntax(String text) {
        logger.info("Starting syntax analysis for text: {}", text);

        // Validazione iniziale del testo.
        validateText(text);
        logger.debug("Text validation successful.");

        // Inizializzazione della frase.
        Sentence sentence = initializeSentence(text);
        logger.debug("Sentence object initialized: {}", sentence);

        // Recupero dei token dall'API.
        List<Token> tokens = fetchTokensFromAPI(text);
        logger.info("Fetched {} tokens from API.", tokens.size());

        // Elaborazione dei token.
        logger.debug("Processing tokens...");
        for (Token token : tokens) {
            logger.debug("Processing token: {}", token);
            processToken(sentence, token);
        }
        logger.info("Token processing completed.");

        // Finalizzazione della frase.
        finalizeSentence(sentence);
        logger.info("Syntax analysis completed. Final structure: {}", sentence.getStructure());

        return sentence;
    }

    private List<Token> fetchTokensFromAPI(String text) {
        logger.debug("Fetching tokens via API for text: {}", text);
        List<Token> tokens = new APIClient<AnalyzeSyntaxResponse>()
                .setSentenceToAPI(text)
                .setAPIType(APIClient.RequestType.SYNTAX)
                .execute()
                .getTokensList();
        logger.debug("Tokens fetched successfully.");
        return tokens;
    }

    private Sentence initializeSentence(String text) {
        logger.debug("Initializing Sentence object for text: {}", text);
        return new Sentence(text);
    }

    private void finalizeSentence(Sentence sentence) {
        logger.debug("Finalizing Sentence object...");
        // Rimuove spazi in eccesso e imposta l'albero sintattico.
        sentence.setStructure(new StringBuilder(sentence.getStructure().toString().trim()));
        sentence.setSyntaxTree(getSyntaxTree(sentence.getSentence().toString()));
        logger.debug("Sentence finalized with structure: {}", sentence.getStructure());
    }

    private void processToken(Sentence sentence, Token token) {
        String word = token.getText().getContent();
        PartOfSpeech.Tag pos = token.getPartOfSpeech().getTag();
        logger.debug("Processing token with word: '{}' and tag: '{}'", word, pos);

        switch (pos) {
            case NOUN:
                addTaggedWord(sentence.getNouns(), sentence.getStructure(), word, NOUN_TAG);
                logger.debug("Token '{}' added as NOUN with tag", word);
                break;
            case VERB:
                addTaggedWord(sentence.getVerbs(), sentence.getStructure(), word, VERB_TAG);
                logger.debug("Token '{}' added as VERB with tag", word);
                break;
            case ADJ:
                addTaggedWord(sentence.getAdjectives(), sentence.getStructure(), word, ADJECTIVE_TAG);
                logger.debug("Token '{}' added as ADJECTIVE with tag", word);
                break;
            case X:
            case UNKNOWN:
                String errorMessage = ERROR_INVALID_TOKEN + ": " + pos + ", word: " + word;
                logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            default:
                sentence.getStructure().append(word).append(" ");
                logger.debug("Token '{}' added without specific tag", word);
        }
    }

    private void addTaggedWord(List<String> wordList, StringBuilder structure, String word, String tag) {
        wordList.add(word);
        structure.append(tag);
        logger.debug("Word '{}' added to list with tag: {}", word, tag);
    }

    public Tree getSyntaxTree(String text) {
        logger.debug("Generating syntax tree for text: {}", text);
        CoreDocument document = pipeline.processToCoreDocument(text);

        if (!document.sentences().isEmpty()) {
            CoreSentence coreSentence = document.sentences().get(0);
            logger.debug("Syntax tree created for the first sentence.");
            return coreSentence.constituencyParse();
        }
        String warningMessage = "No sentences found for syntax tree generation.";
        logger.warn(warningMessage);
        return null;
    }

    private void validateText(String text) {
        logger.debug("Validating text: {}", text);

        if (text == null || text.isEmpty()) {
            String errorMessage = "Input text cannot be null or empty.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        int maxLength = Integer.parseInt(ConfigManager.getInstance().getProperty("max.sentence.length"));
        if (text.length() > maxLength) {
            String errorMessage = "Input text cannot exceed " + maxLength + " characters.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (text.trim().isEmpty()) {
            String errorMessage = "Input text cannot be empty or whitespace only.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (!text.matches(".*[a-zA-Z]+.*")) {
            String errorMessage = "Input text must contain at least one alphabetical character.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (text.matches("^[^a-zA-Z0-9\\s].*")) {
            String errorMessage = "Input text contains invalid characters at the start of the text.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (text.matches(".*[^a-zA-Z0-9.\\s]$")) {
            String errorMessage = "Input text contains invalid characters at the end of the text.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (text.matches(".*[^a-zA-Z0-9.,:'\"\\s].*")) {
            String errorMessage = "Input text contains invalid characters.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        logger.debug("Text validation successful.");
    }
}