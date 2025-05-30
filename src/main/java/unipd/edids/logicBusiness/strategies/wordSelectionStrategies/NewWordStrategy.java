package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.WordFactory;

public class NewWordStrategy implements WordSelectionStrategy {

    private static final Logger logger = LogManager.getLogger(NewWordStrategy.class);

    @Override
       public void populateWords(Sentence temp) {


           logger.info("Populating Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}", temp.getNouns(), temp.getVerbs(), temp.getAdjectives());

        // Riempie i nouns, verbs, adjectives con nuove parole
        while (temp.getNouns().size() < StringUtils.countMatches(temp.getStructure(), "[noun]")) {
            temp.getNouns().add(WordFactory.getWordProvider(WordFactory.WordType.NOUN).getRandomWord());
        }
        while (temp.getVerbs().size() < StringUtils.countMatches(temp.getStructure(), "[verb]")) {
            temp.getVerbs().add(WordFactory.getWordProvider(WordFactory.WordType.VERB).getRandomWord());
        }
        while (temp.getAdjectives().size() < StringUtils.countMatches(temp.getStructure(), "[adjective]")) {
            temp.getAdjectives().add(WordFactory.getWordProvider(WordFactory.WordType.ADJECTIVE).getRandomWord());
        }
        logger.info("Populating Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}", temp.getNouns(), temp.getVerbs(), temp.getAdjectives());
    }
   }