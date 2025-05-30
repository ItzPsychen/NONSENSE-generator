package unipd.edids.logicBusiness.strategies.structureStrategies;

import unipd.edids.logicBusiness.entities.Sentence;

public class SameAsAnalyzedStructureStrategy implements StructureSentenceStrategy{
    private final Sentence inputSentence; // Campo per memorizzare il parametro esterno

    public SameAsAnalyzedStructureStrategy(Sentence inputSentence) {
        this.inputSentence = inputSentence; // Inizializzazione del valore
    }
    @Override
    public StringBuilder generateSentenceStructure() {
        return new StringBuilder(inputSentence.getStructure().toString());
    }
}
