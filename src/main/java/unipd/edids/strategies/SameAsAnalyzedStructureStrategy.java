package unipd.edids.strategies;

import unipd.edids.Sentence;

public class SameAsAnalyzedStructureStrategy implements StructureSentenceStrategy{
    private final Sentence inputSentence; // Campo per memorizzare il parametro esterno

    public SameAsAnalyzedStructureStrategy(Sentence inputSentence) {
        this.inputSentence = inputSentence; // Inizializzazione del valore
    }
    @Override
    public StringBuilder generateSentence() {
        return new StringBuilder(inputSentence.getStructure().toString());
    }
}
