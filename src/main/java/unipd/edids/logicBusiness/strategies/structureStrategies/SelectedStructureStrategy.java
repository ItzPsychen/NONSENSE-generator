package unipd.edids.logicBusiness.strategies.structureStrategies;

/**
 * Represents a strategy that uses a preselected sentence structure for sentence generation.
 *
 * <p>Responsibilities:</p>
 * - Maintains a predefined sentence structure provided during initialization.
 * - Implements the mechanism for generating sentences with a consistent, specific structure.
 *
 * <p>Design Pattern:</p>
 * - Implements the Strategy Design Pattern, allowing flexibility in swapping sentence structure generation algorithms.
 */
public class SelectedStructureStrategy implements StructureSentenceStrategy{
    private final String selectedStructure; // Campo per memorizzare il parametro esterno

    /**
     * Constructor for initializing the SelectedStructureStrategy with a predefined sentence structure.
     *
     * @param selectedStructure The selected structure to be used for sentence generation. Must not be null.
     * @throws IllegalArgumentException If the selectedStructure parameter is null.
     */
    public SelectedStructureStrategy( String selectedStructure) {
        if (selectedStructure == null)
            throw new IllegalArgumentException("The selected structure cannot be null.");
        this.selectedStructure = selectedStructure; // Inizializzazione del valore
    }
    /**
     * Generates the predefined sentence structure as a mutable StringBuilder object.
     *
     * @return A StringBuilder initialized with the predefined sentence structure.
     */
    @Override
    public StringBuilder generateSentenceStructure() {
        return new StringBuilder(selectedStructure);
    }
}
