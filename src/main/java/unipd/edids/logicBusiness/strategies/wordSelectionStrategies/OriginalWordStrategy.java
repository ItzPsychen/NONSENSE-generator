package unipd.edids.logicBusiness.strategies.wordSelectionStrategies;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unipd.edids.logicBusiness.entities.Sentence;
import unipd.edids.logicBusiness.entities.Verb;
import unipd.edids.logicBusiness.factories.WordFactory;
import unipd.edids.logicBusiness.strategies.tenseStrategies.FutureTenseStrategy;

import java.util.ArrayList;
import java.util.Collections;

public class OriginalWordStrategy implements WordSelectionStrategy {
    private static final Logger logger = LogManager.getLogger(OriginalWordStrategy.class);
    Sentence inputSentence;

    public OriginalWordStrategy(Sentence inputSentence) {
        this.inputSentence = inputSentence;
    }

    @Override
    public void populateWords(Sentence temp) {
        temp.setNouns(new ArrayList<>(inputSentence.getNouns()));
        temp.setVerbs(new ArrayList<>(inputSentence.getVerbs()));
        temp.setAdjectives(new ArrayList<>(inputSentence.getAdjectives()));

        while (StringUtils.countMatches(temp.getStructure(), "[noun]") - temp.getNouns().size() > 0) {
            temp.getNouns().add(WordFactory.getWordProvider(WordFactory.WordType.NOUN).getRandomWord());
        }
        if(Verb.getInstance().getTenseStrategy() instanceof FutureTenseStrategy){
            ArrayList<String> temporaryVerbs = new ArrayList<>(temp.getVerbs());
            temp.getVerbs().clear();
            for (String verb : temporaryVerbs) {
                temp.getVerbs().add(Verb.getInstance().conjugate(verb));
            }
        }
        while (StringUtils.countMatches(temp.getStructure(), "[verb]") - temp.getVerbs().size() > 0) {
            temp.getVerbs().add(WordFactory.getWordProvider(WordFactory.WordType.VERB).getRandomWord());
        }
        while (StringUtils.countMatches(temp.getStructure(), "[adjective]") - temp.getAdjectives().size() > 0) {
            temp.getAdjectives().add(WordFactory.getWordProvider(WordFactory.WordType.ADJECTIVE).getRandomWord());
        }
        logger.info("Populated Word Lists - Nouns: {}, Verbs: {}, Adjectives: {}", temp.getNouns(), temp.getVerbs(), temp.getAdjectives());
        Collections.shuffle(temp.getNouns());
        Collections.shuffle(temp.getVerbs());
        Collections.shuffle(temp.getAdjectives());

    }
}