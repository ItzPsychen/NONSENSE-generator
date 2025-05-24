package unipd.edids;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.entities.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class GenerateSentenceService {
    private static final Logger logger = LogManager.getLogger(GenerateSentenceService.class);
    private static final int MAX_RECURSION_DEPTH = 3;
    private Word nounProvider;
    private Word verbProvider;
    private Word adjectiveProvider;
    SentenceStructure structureProvider;

    private Sentence temp;

    public GenerateSentenceService() {
        temp = new Sentence();
        this.nounProvider = Noun.getInstance();
        this.verbProvider = Verb.getInstance();
        this.adjectiveProvider = Adjective.getInstance();
        this.structureProvider = new SentenceStructure();
    }

    public Sentence generateSentence(Sentence inputSentence) {
        System.out.println("[generateSentence] " + inputSentence.getSentence().toString());
        if (inputSentence.getSentence().toString() == null || inputSentence.getSentence().toString().trim().isEmpty() ||
                !inputSentence.getSentence().toString().matches(".[a-zA-Z]+.")) {

            // TODO
            // scrivere una frase con termini poco adatti
            // fare l'analisi
            // scrivere una frase adatta
            // fare l'analisi
            // fare la generazione
            // ERRORE: entra qui per nessuna ragione

            System.out.println(" -> null");
            return null;
        }
        temp.setNouns(new ArrayList<>(inputSentence.getNouns()));
        temp.setVerbs(new ArrayList<>(inputSentence.getVerbs()));
        temp.setAdjectives(new ArrayList<>(inputSentence.getAdjectives()));
        System.out.println(temp.getNouns());

        // Step 1: Estrarre la struttura della frase
        temp.setStructure(new StringBuilder(resolveTemplate(0)));
        logger.info("Initial Sentence Structure: {}", temp.getStructure());

        // Step 2: Verifica e caricamento delle liste (nouns, verbs, adjectives)
        populateWordLists();

        // Step 3: Shuffla le liste per diversificare l'output
        shuffleWordLists();

        // Step 4: Sostituire i placeholder nella struttura con parole effettive
        temp.setSentence(new StringBuilder(replacePlaceholders()));

        logger.info("Final Sentence: {}", temp.getSentence());

        return temp;
    }

    private String resolveTemplate(int depth) {
        String template = structureProvider.getRandomStructure();
        if (depth > MAX_RECURSION_DEPTH) {
            return "[noun]";
        }
        while (template.contains("[sentence]")) {
            template = template.replaceFirst("\\[sentence\\]", resolveTemplate(depth + 1));
        }

        return template;
    }

    private void populateWordLists() {
        while (StringUtils.countMatches(temp.getStructure(), "[noun]") - temp.getNouns().size() > 0) {
            temp.getNouns().add(nounProvider.getRandomWord());
        }
        while (StringUtils.countMatches(temp.getStructure(), "[verb]") - temp.getVerbs().size() > 0) {
            temp.getVerbs().add(verbProvider.getRandomWord());
        }
        while (StringUtils.countMatches(temp.getStructure(), "[adjective]") - temp.getAdjectives().size() > 0) {
            temp.getAdjectives().add(adjectiveProvider.getRandomWord());
        }
        logger.info("Populated Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}", temp.getNouns(), temp.getVerbs(), temp.getAdjectives());
    }

    private void shuffleWordLists() {
        Collections.shuffle(temp.getNouns());
        Collections.shuffle(temp.getVerbs());
        Collections.shuffle(temp.getAdjectives());
        logger.info("Shuffled Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}", temp.getNouns(), temp.getVerbs(), temp.getAdjectives());
    }

    private String replacePlaceholders() {
        String result = temp.getStructure().toString(); // Converte lo StringBuilder iniziale
        Iterator<String> nounsIterator = temp.getNouns().iterator();
        Iterator<String> verbsIterator = temp.getVerbs().iterator();
        Iterator<String> adjectiveIterator = temp.getAdjectives().iterator();

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
