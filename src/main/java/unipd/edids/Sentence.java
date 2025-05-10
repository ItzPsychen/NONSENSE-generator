package unipd.edids;

import com.google.cloud.language.v1.Token;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Sentence {
    private static final Logger logger = LogManager.getLogger(Sentence.class);
    private static final int MAX_RECURSION_DEPTH = 3;
    private Word nounProvider;
    private Word verbProvider;
    private Word adjectiveProvider;
    SentenceStructure structureProvider;


    private StringBuilder sentence;                // frase originale o generata
    private StringBuilder structure;                // frase con i placeholder tipo "The [NOUN] [VERB]..."
    private List<String> nouns;
    private List<String> verbs;
    private List<String> adjectives;

    // Costruttori, getter, setter

    public Sentence() {
        this.sentence = new StringBuilder();
        this.structure = new StringBuilder();
        this.nouns = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.adjectives = new ArrayList<>();
        this.nounProvider = Noun.getInstance();
        this.verbProvider = Verb.getInstance();
        this.adjectiveProvider = Adjective.getInstance();
        this.structureProvider = new SentenceStructure();
    }

    public Sentence(String text) {
        this.sentence = new StringBuilder(text);
        this.structure = new StringBuilder();
        this.nouns = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.adjectives = new ArrayList<>();
        List<Token> tokens = AnalyzeSyntaxService.analyzeSyntax(text);
        if (tokens != null) {
            analyzeFromTokens(tokens);
        }
        this.nounProvider = Noun.getInstance();
        this.verbProvider = Verb.getInstance();
        this.adjectiveProvider = Adjective.getInstance();
        this.structureProvider = new SentenceStructure();
    }

    public StringBuilder getSentence() {
        return sentence;
    }

    public StringBuilder getStructure() {
        return structure;
    }

    public Word getNounProvider() {
        return nounProvider;
    }

    public void setNounProvider(Word nounProvider) {
        this.nounProvider = nounProvider;
    }

    public Word getVerbProvider() {
        return verbProvider;
    }

    public void setVerbProvider(Word verbProvider) {
        this.verbProvider = verbProvider;
    }

    public Word getAdjectiveProvider() {
        return adjectiveProvider;
    }

    public void setAdjectiveProvider(Word adjectiveProvider) {
        this.adjectiveProvider = adjectiveProvider;
    }

    public SentenceStructure getStructureProvider() {
        return structureProvider;
    }

    public void setStructureProvider(SentenceStructure structureProvider) {
        this.structureProvider = structureProvider;
    }

    public void setSentence(StringBuilder sentence) {
        this.sentence = sentence;
    }

    public void setStructure(StringBuilder structure) {
        this.structure = structure;
    }

    public void setNouns(List<String> nouns) {
        this.nouns = nouns;
    }

    public void setVerbs(List<String> verbs) {
        this.verbs = verbs;
    }

    public void setAdjectives(List<String> adjectives) {
        this.adjectives = adjectives;
    }

    public List<String> getNouns() {
        return nouns;
    }

    public List<String> getVerbs() {
        return verbs;
    }

    public List<String> getAdjectives() {
        return adjectives;
    }

    public void analyzeFromTokens(List<Token> tokens) {
        this.structure = new StringBuilder();
        for (Token token : tokens) {
            String word = token.getText().getContent();
            String pos = String.valueOf(token.getPartOfSpeech().getTag());
            logger.info("Parola: {}, Parte del discorso: {}", word, pos);
            switch (pos) {
                case "NOUN":
                    nouns.add(word);
                    structure.append("[noun] ");
                    break;
                case "VERB":
                    verbs.add(word);
                    structure.append("[verb] ");
                    break;
                case "ADJ":
                    adjectives.add(word);
                    structure.append("[adjective] ");
                    break;
                default:
                    structure.append(word).append(" ");
            }
        }
        this.structure = new StringBuilder(structure.toString().trim());
        System.out.println(structure);
    }

    public String generateSentence() {
        // Step 1: Estrarre la struttura della frase
        structure = new StringBuilder(resolveTemplate(0));
        logger.info("Initial Sentence Structure: {}", structure);

        // Step 2: Verifica e caricamento delle liste (nouns, verbs, adjectives)
        populateWordLists();

        // Step 3: Shuffla le liste per diversificare l'output
        shuffleWordLists();

        // Step 4: Sostituire i placeholder nella struttura con parole effettive
        sentence = new StringBuilder(replacePlaceholders());

        logger.info("Final Sentence: {}", sentence);
        return sentence.toString();
    }


    private String resolveTemplate(int depth) {
        String temp = structureProvider.getRandomStructure();
        if (depth > MAX_RECURSION_DEPTH) {
            return "[noun]";
        }
        while (temp.contains("[sentence]")) {
            temp = temp.replaceFirst("\\[sentence\\]", resolveTemplate(depth + 1));
        }

        return temp;

    }

    private void populateWordLists() {

        while (StringUtils.countMatches(structure, "[noun]") - nouns.size() > 0) {
            nouns.add(nounProvider.getRandomWord());
        }
        while (StringUtils.countMatches(structure, "[verb]") - verbs.size() > 0) {
            verbs.add(verbProvider.getRandomWord());
        }
        while (StringUtils.countMatches(structure, "[adjective]") - adjectives.size() > 0) {
            adjectives.add(adjectiveProvider.getRandomWord());
        }
        logger.info("Populated Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}", nouns, verbs, adjectives);
    }

    private void shuffleWordLists() {
        Collections.shuffle(nouns);
        Collections.shuffle(verbs);
        Collections.shuffle(adjectives);
        logger.info("Shuffled Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}", nouns, verbs, adjectives);
    }

    private String replacePlaceholders() {
        String result = structure.toString(); // Converte lo StringBuilder iniziale
        Iterator<String> nounsIterator = nouns.iterator();
        Iterator<String> verbsIterator = verbs.iterator();
        Iterator<String> adjectiveIterator = adjectives.iterator();

        while (result.contains("[noun]") && nounsIterator.hasNext()) {
            result = result.replaceFirst("\\[noun\\]", nounsIterator.next());
        }
        while (result.contains("[verb]") && verbsIterator.hasNext()) {
            result = result.replaceFirst("\\[verb\\]", verbsIterator.next());
        }
        while (result.contains("[adjective]") && adjectiveIterator.hasNext()) {
            result = result.replaceFirst("\\[adjective\\]", adjectiveIterator.next());
        }
        logger.info("Sentence after placeholders replacement: {}", result);
        return result;
    }


}
