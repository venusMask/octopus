package org.venus.octopus.api.agent;

import java.util.Map;
import java.util.Optional;

/**
 * Agent state interface.
 * <p>
 * AgentState is the core carrier for data passed between nodes in the graph
 * flow orchestration, essentially a type-safe key-value store. Each node
 * receives the current state, and after execution, returns an updated state
 * slice, which is automatically merged into the global state by the framework.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 *
 * <pre>{@code
 * AgentState state = new MapAgentState();
 * state.put("messages", List.of(new HumanMessage("Hello")));
 * state.put("next", "agent");
 *
 * String next = state.get("next"); // "agent"
 * }</pre>
 */
public interface AgentState {

    /**
     * Gets a value by key.
     *
     * @param key
     *            The key
     * @return The corresponding value, or null if it does not exist
     */
    <T> T get(String key);

    /**
     * Safely gets a value by key, returning an Optional.
     *
     * @param key
     *            The key
     * @return An Optional containing the value
     */
    <T> Optional<T> getOptional(String key);

    /**
     * Sets a key-value pair.
     *
     * @param key
     *            The key
     * @param value
     *            The value
     */
    void put(String key, Object value);

    /**
     * Batch merges key-value pairs (merges all entries from updates into the
     * current state).
     *
     * @param updates
     *            The state updates to merge
     */
    void merge(Map<String, Object> updates);

    /**
     * Converts the current state to an immutable Map view.
     *
     * @return A Map representation of the state
     */
    Map<String, Object> toMap();

    /**
     * Checks if the specified key is present.
     *
     * @param key
     *            The key
     * @return True if present, false otherwise
     */
    boolean containsKey(String key);

    /**
     * Creates a shallow copy of the current state.
     *
     * @return A copy of the state
     */
    AgentState copy();
}
