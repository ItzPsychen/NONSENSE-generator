package unipd.edids;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.entities.*;
import unipd.edids.strategies.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

//Fix usare una factory con singleton, usare una strategy per l'estrazione delle strutture

public class GenerateSentenceService implements ConfigObserver {

    StructureSentenceStrategy structureSentenceStrategy;
    WordSelectionStrategy wordSelectionStrategy;

    private static final Logger logger = LogManager.getLogger(GenerateSentenceService.class);


//fix temp?
    private Sentence temp;

    public GenerateSentenceService() {
        temp = new Sentence();
        structureSentenceStrategy = new RandomStructureStrategy();
    }

    public void setStructureSentenceStrategy(StructureSentenceStrategy strategy) {
        logger.info("Setting Structure Sentence Strategy: {}", strategy.getClass().getSimpleName());
        this.structureSentenceStrategy = strategy;
    }

    public StructureSentenceStrategy getStructureSentenceStrategy() {
        return structureSentenceStrategy;
    }

    public void setWordsStrategi(WordSelectionStrategy strategy) {
        this.wordSelectionStrategy = strategy;
    }

    public Sentence generateSentence() {
        temp = new Sentence();
        // Step 1: Estrarre la struttura della frase
        logger.warn(structureSentenceStrategy.getClass().getSimpleName());
        temp.setStructure(structureSentenceStrategy.generateSentence());
        if(ConfigManager.getInstance().getProperty("allow.recursive.sentences").equals("true")){
            temp.setStructure(new StringBuilder(resolveTemplate(temp.getStructure().toString(),0, Integer.parseInt(ConfigManager.getInstance().getProperty("max.recursion.level")))));
        } else if ( temp.getStructure().toString().contains("[sentence]")) {
            temp.setStructure(new StringBuilder(temp.getStructure().toString().replaceAll("\\[sentence\\]", "[noun]")));
        }

        logger.info("Initial Sentence Structure: {}", temp.getStructure());

        logger.error("Word Selection Strategy: {}", wordSelectionStrategy.getClass().getSimpleName());
        wordSelectionStrategy.populateWords(temp);
//        // Step 2: Verifica e caricamento delle liste (nouns, verbs, adjectives)
//        populateWordLists();
//
//        // Step 3: Shuffla le liste per diversificare l'output
//        shuffleWordLists();

        // Step 4: Sostituire i placeholder nella struttura con parole effettive
        temp.setSentence(new StringBuilder(replacePlaceholders()));

        temp.getSentence().setCharAt(0, Character.toUpperCase(temp.getSentence().charAt(0)));
        logger.info("Final Sentence: {}", temp.getSentence());

        return temp;
    }

    private String resolveTemplate(String template, int depth, int maxRecursionDepth) {
        if (depth > maxRecursionDepth) {
            return "[noun]";
        }
        while (template.contains("[sentence]")) {
            template = template.replaceFirst("\\[sentence\\]", resolveTemplate(template, depth + 1, maxRecursionDepth));
        }

        return template;
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

    @Override
    public void onConfigChange(String key, String value) {

    }


}
