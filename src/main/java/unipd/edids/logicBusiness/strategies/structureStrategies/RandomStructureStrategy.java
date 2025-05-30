package unipd.edids.logicBusiness.strategies.structureStrategies;

import unipd.edids.logicBusiness.entities.SentenceStructure;

public class RandomStructureStrategy implements StructureSentenceStrategy{
    @Override
    public StringBuilder generateSentenceStructure() {
        return new StringBuilder(SentenceStructure.getInstance().getRandomStructure());
    }
}
