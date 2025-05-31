package unipd.edids.logicBusiness.strategies.structureStrategies;

/**
 * Strategy interface for generating sentence structures.
 *
 * <p>Responsibilities:</p>
 * - Defines the contract for generating sentence structures as mutable strings.
 * - Supports variability and flexibility in sentence generation by enabling multiple implementation strategies.
 *
 * <p>Design Pattern:</p>
 * - Follows the Strategy Design Pattern, allowing interchangeable algorithms for sentence structure generation.
 */
public interface StructureSentenceStrategy {
    /**
     * Generates the structure of a sentence in the form of a mutable string.
     *
     * @return A StringBuilder representing the generated sentence structure.
     */
    StringBuilder generateSentenceStructure();
}
