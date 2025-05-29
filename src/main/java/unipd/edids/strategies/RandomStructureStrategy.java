package unipd.edids.strategies;

import unipd.edids.SentenceStructure;

public class RandomStructureStrategy implements StructureSentenceStrategy{
    @Override
    public StringBuilder generateSentence() {
        System.out.println("Generated structure: " +  SentenceStructure.getInstance().getStructures());
        return new StringBuilder(SentenceStructure.getInstance().getRandomStructure());
    }
}
