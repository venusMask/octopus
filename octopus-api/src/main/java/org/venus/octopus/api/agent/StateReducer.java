package org.venus.octopus.api.agent;

/**
 * Functional interface for state reduction.
 * <p>
 * Defines how values for the same key are merged during multiple state updates.
 * For example, a message list should be appended rather than overwritten:
 * </p>
 *
 * <pre>{@code
 * // Message list append strategy
 * StateReducer<List<Message>> appendReducer = (existing, newValue) -> {
 *     List<Message> merged = new ArrayList<>(existing);
 *     merged.addAll(newValue);
 *     return merged;
 * };
 * }</pre>
 *
 * @param <T>
 *            Value type
 */
@FunctionalInterface
public interface StateReducer<T> {

    /**
     * Reduces two values into one.
     *
     * @param existing
     *            The existing value
     * @param newValue
     *            The new value
     * @return The reduced value
     */
    T reduce(T existing, T newValue);
}
