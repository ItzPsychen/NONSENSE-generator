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
    private Word nounProvider;
    private Word verbProvider;
    private Word adjectiveProvider;


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
    }

    public StringBuilder getSentence() {
        return sentence;
    }

    public StringBuilder getStructure() {
        return structure;
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
        // qui selezioni random parole dalle liste (o da dizionario extra)
        // e sostituisci in structure
        SentenceStructure structureProvider = new SentenceStructure();
        structure = new StringBuilder(structureProvider.getRandomStructure());
        System.out.println(structure);
        logger.info("Necessary Nouns: {}, Available: {}",StringUtils.countMatches(structure, "[noun]"), nouns.size());
        while (StringUtils.countMatches(structure, "[noun]") - nouns.size() > 0){
            nouns.add(nounProvider.getRandomWord());
        }
        while (StringUtils.countMatches(structure, "[verb]") - verbs.size() > 0){
            verbs.add(verbProvider.getRandomWord());
        }
        while (StringUtils.countMatches(structure, "[adjective]") - adjectives.size() > 0){
            adjectives.add(adjectiveProvider.getRandomWord());
        }
        logger.info("Necessary Nouns: {}, Available: {}",StringUtils.countMatches(structure, "[noun]"), nouns.size());
        logger.info(nouns.toString());
        sentence = new StringBuilder(structure);
        String result = structure.toString();
        Collections.shuffle(nouns);
        Collections.shuffle(verbs);
        Collections.shuffle(adjectives);
        Iterator<String> nounsIterator = nouns.iterator();
        Iterator<String> verbsIterator = verbs.iterator();
        Iterator<String> adjectiveIterator = adjectives.iterator();
        logger.warn(verbs.toString());
        while (result.contains("[noun]"))
            result = result.replaceFirst("\\[noun\\]", nounsIterator.next());
        while (result.contains("[verb]"))
            result = result.replaceFirst("\\[verb\\]", verbsIterator.next());
        while (result.contains("[adjective]"))
            result = result.replaceFirst("\\[adjective\\]", adjectiveIterator.next());
        logger.info("Sentence: {}", result);
        return null;
    }


}
