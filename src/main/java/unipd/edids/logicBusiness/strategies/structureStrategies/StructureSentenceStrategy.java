package unipd.edids.logicBusiness.strategies.structureStrategies;

/**
 * Represents a strategy for defining the sentence structure during sentence generation.
 *
 * <p>Responsibilities:</p>
 * - Abstracts the process of determining how a sentence's structure is configured.
 * - Provides mechanism to implement different strategies to define sentence structures.
 *
 * <p>Design Pattern:</p>
 * - Implements the Strategy Design Pattern, enabling interchangeable algorithms for sentence structure generation.
 */
public interface StructureSentenceStrategy {
    public StringBuilder generateSentenceStructure();
}
