package unipd.edids.logicBusiness.strategies.structureStrategies;


import unipd.edids.logicBusiness.entities.SentenceStructure;

/**
 * Implements a strategy for generating sentences with randomly selected sentence structures.
 *
 * <p>Responsibilities:</p>
 * - Retrieves and uses a random sentence structure from a predefined set managed by the SentenceStructure class.
 * - Ensures variability in sentence structure generation through randomized selection.
 *
 * <p>Design Pattern:</p>
 * - Implements the Strategy Design Pattern, enabling interchangeable sentence structure generation algorithms.
 */
public class RandomStructureStrategy implements StructureSentenceStrategy{
    /**
     * Generates a mutable string representation of a randomly selected sentence structure.
     *
     * @return A StringBuilder containing the randomly chosen sentence structure.
     */
    @Override
    public StringBuilder generateSentenceStructure() {
        return new StringBuilder(SentenceStructure.getInstance().getRandomStructure());
    }
}
