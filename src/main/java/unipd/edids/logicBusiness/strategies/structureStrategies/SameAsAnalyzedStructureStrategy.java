package unipd.edids.logicBusiness.strategies.structureStrategies;

import unipd.edids.logicBusiness.entities.Sentence;

/**
 * Implements a strategy to reuse the structure of a given input sentence for sentence generation.
 *
 * <p>Responsibilities:</p>
 * - Utilizes the structure of an existing input sentence.
 * - Implements a mechanism to directly copy the analyzed structure.
 *
 * <p>Design Pattern:</p>
 * - Implements the Strategy Design Pattern, allowing flexibility in sentence structure generation approaches.
 */
public class SameAsAnalyzedStructureStrategy implements StructureSentenceStrategy{
    private final Sentence inputSentence; // Campo per memorizzare il parametro esterno

    /**
     * Constructs the SameAsAnalyzedStructureStrategy with a given input sentence.
     *
     * @param inputSentence The input sentence whose analyzed structure will be reused for sentence generation.
     *                       Must not be null.
     * @throws IllegalArgumentException If the inputSentence parameter is null.
     */
    public SameAsAnalyzedStructureStrategy(Sentence inputSentence) {
        if (inputSentence == null)
            throw new IllegalArgumentException("The input sentence cannot be null.");
        this.inputSentence = inputSentence; // Inizializzazione del valore
    }
    /**
     * Generates the sentence structure using the structure from the input sentence.
     *
     * @return A StringBuilder initialized with the structure of the provided input sentence.
     */
    @Override
    public StringBuilder generateSentenceStructure() {
        return new StringBuilder(inputSentence.getStructure().toString());
    }
}
