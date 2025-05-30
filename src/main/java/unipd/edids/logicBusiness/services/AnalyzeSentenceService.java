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

/**
 * Service class for analyzing sentence syntax and structure.
 *
 * <p> Responsibilities:
 * - Perform syntax analysis on a given text, identifying grammatical structures such as nouns, verbs, and adjectives.
 * - Use the StanfordCoreNLP pipeline to process and analyze text.
 * - Validate input text and handle various constraints like length, format, and content.
 * - Organize extracted data (e.g., tokens) into categories (e.g., nouns, verbs, adjectives) and maintain a sentence structure.
 * - Generate syntax trees based on the parsed input text.
 *
 * <p> Design Pattern:
 * - Singleton design pattern is employed for the StanfordCoreNLP pipeline initialization to ensure efficient resource usage.
 */
public class AnalyzeSentenceService {
    /**
     * Logger used for logging information, warnings, and errors within the AnalyzeSentenceService class.
     */
    private static final Logger logger = LogManager.getLogger(AnalyzeSentenceService.class);

    /**
     * Represents the tagging label used to identify noun elements in the sentence being processed.
     */
    private static final String NOUN_TAG = "[noun] ";
    /**
     * Represents the tagging label used to identify verb elements in the sentence being processed.
     */
    private static final String VERB_TAG = "[verb] ";
    /**
     * Represents the tagging label used to identify adjective elements in the sentence being processed.
     */
    private static final String ADJECTIVE_TAG = "[adjective] ";
    /**
     * Error message indicating that the provided token does not align with the expected grammatical type.
     */
    private static final String ERROR_INVALID_TOKEN = "Invalid token for grammatical type";

    /**
     * Represents the StanfordCoreNLP pipeline instance used for natural language processing tasks within the service.
     */
    private final StanfordCoreNLP pipeline;


    /**
     * Initializes the StanfordCoreNLP pipeline with the required annotators.
     */
    public AnalyzeSentenceService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse");
        this.pipeline = new StanfordCoreNLP(props);
        logger.info("StanfordCoreNLP pipeline initialized with properties: {}", props);
    }

    /**
     * Performs syntax analysis for a given input text and returns a structured representation
     * of the analyzed sentence.
     *
     * @param text The input text to be analyzed. Must be non-null, non-empty, and adhere
     *             to validation rules (e.g., acceptable characters and length).
     * @return A {@code Sentence} object containing the structure, tokens, and metadata
     *         derived from the syntax analysis.
     */
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

    /**
     * Fetches a list of tokens from an external API by analyzing the syntax of the provided text.
     *
     * @param text The input text to be sent to the API for syntax analysis.
     * @return A list of tokens extracted from the input text.
     */
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

    /**
     * Responsible for initializing a Sentence object with the provided text.
     *
     * @param text the text to initialize the Sentence object with.
     * @return a new Sentence object initialized with the given text.
     */
    private Sentence initializeSentence(String text) {
        logger.debug("Initializing Sentence object for text: {}", text);
        return new Sentence(text);
    }

    /**
     * Analyzes and performs the necessary finalization on a {@code Sentence} object.
     * This includes trimming the {@code structure} and applying the syntax tree.
     *
     * @param sentence The {@code Sentence} object to be finalized, containing
     *                 its textual structure and syntax tree.
     */
    private void finalizeSentence(Sentence sentence) {
        logger.debug("Finalizing Sentence object...");
        // Rimuove spazi in eccesso e imposta l'albero sintattico.
        sentence.setStructure(new StringBuilder(sentence.getStructure().toString().trim()));
        sentence.setSyntaxTree(getSyntaxTree(sentence.getSentence().toString()));
        logger.debug("Sentence finalized with structure: {}", sentence.getStructure());
    }

    /**
     * Processes a single token by analyzing its part of speech and adding it to the appropriate components
     * of a Sentence object. Updates the sentence structure and logs relevant information.
     *
     * @param sentence The Sentence object where the token's details are added.
     * @param token    The Token object containing the text and part of speech to be processed.
     */
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

    /**
     * Adds a word along with its associated tag to given data structures and logs the operation.
     *
     * @param wordList The list to which the word is added.
     * @param structure The StringBuilder used to append the tag.
     * @param word The word to be added.
     * @param tag The tag to be associated with the word.
     */
    private void addTaggedWord(List<String> wordList, StringBuilder structure, String word, String tag) {
        wordList.add(word);
        structure.append(tag);
        logger.debug("Word '{}' added to list with tag: {}", word, tag);
    }

    /**
     * Generates a syntax tree for the first sentence in the given text.
     *
     * @param text the input text to be analyzed for syntax tree generation.
     * @return the syntax tree of the first sentence if available, otherwise null.
     */
    public Tree getSyntaxTree(String text) {
        logger.debug("Generating syntax tree for text: {}", text);
        CoreDocument document = pipeline.processToCoreDocument(text);

        if (!document.sentences().isEmpty()) {
            CoreSentence coreSentence = document.sentences().getFirst();
            logger.debug("Syntax tree created for the first sentence.");
            return coreSentence.constituencyParse();
        }
        String warningMessage = "No sentences found for syntax tree generation.";
        logger.warn(warningMessage);
        return null;
    }

    /**
     * Validates the input text based on various constraints, ensuring it adheres to predefined structure and content rules.
     *
     * @param text the input string to validate; cannot be null, empty, or non-compliant with the specified validation criteria.
     */
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

    }
}