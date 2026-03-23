package org.venus.octopus.core.state;

import java.util.*;
import org.venus.octopus.api.agent.AgentState;

/**
 * AgentState implementation based on HashMap.
 * <p>
 * Provides a simple, thread-unsafe state store, suitable for single-threaded
 * graph execution scenarios. If thread safety is required, replace the
 * underlying Map with ConcurrentHashMap.
 * </p>
 */
public class MapAgentState implements AgentState {

    private final Map<String, Object> data;

    public MapAgentState() {
        this.data = new HashMap<>();
    }

    public MapAgentState(Map<String, Object> data) {
        this.data = new HashMap<>(data);
    }

    /**
     * Factory method to create a state from a single key-value pair.
     */
    public static MapAgentState of(String key, Object value) {
        MapAgentState state = new MapAgentState();
        state.put(key, value);
        return state;
    }

    /**
     * Factory method to create a state from a Map.
     */
    public static MapAgentState from(Map<String, Object> map) {
        return new MapAgentState(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptional(String key) {
        return Optional.ofNullable((T) data.get(key));
    }

    @Override
    public void put(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public void merge(Map<String, Object> updates) {
        if (updates != null) {
            data.putAll(updates);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(data);
    }

    @Override
    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    @Override
    public AgentState copy() {
        return new MapAgentState(data);
    }

    /**
     * Returns a mutable copy of the current state, supporting chained writes.
     */
    public MapAgentState with(String key, Object value) {
        MapAgentState copy = new MapAgentState(data);
        copy.put(key, value);
        return copy;
    }

    @Override
    public String toString() {
        return "MapAgentState" + data;
    }
}
