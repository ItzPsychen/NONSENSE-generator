package unipd.edids.strategies;

import unipd.edids.SentenceStructure;

public class RandomStructureStrategy implements StructureSentenceStrategy{
    @Override
    public StringBuilder generateSentence() {
        return new StringBuilder(SentenceStructure.getInstance().getRandomStructure());
    }
}
