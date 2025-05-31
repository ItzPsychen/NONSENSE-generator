package unipd.edids.logicBusiness.observers.configObserver;

/**
 * The ConfigObserver interface defines the contract for observing and responding to configuration changes.
 *
 * <p>Responsibilities:
 * - Defines a standardized method to handle configuration changes dynamically.
 * - Allows implementing classes to respond to updates in configuration properties, facilitating adaptability.
 *
 * <p>Design Pattern:
 * - Observer Pattern: Provides a mechanism for implementing objects to observe and react to changes in configuration.
 */
public interface ConfigObserver {
    /**
     * Reacts to a change in configuration by notifying the observer with the updated configuration key and value.
     *
     * @param key   The configuration property key that has been changed.
     * @param value The new value of the configuration property.
     */
    void onConfigChange(String key, String value);
}
