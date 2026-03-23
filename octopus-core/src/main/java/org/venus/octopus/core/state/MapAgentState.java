package org.venus.octopus.core.state;

import java.util.*;
import org.venus.octopus.api.agent.AgentState;

/**
 * 基于 HashMap 的 AgentState 实现
 * <p>
 * 提供线程不安全的简单状态存储，适用于单线程图执行场景。
 * 如需线程安全，可替换底层 Map 为 ConcurrentHashMap。
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
     * 工厂方法，从单个键值创建状态
     */
    public static MapAgentState of(String key, Object value) {
        MapAgentState state = new MapAgentState();
        state.put(key, value);
        return state;
    }

    /**
     * 工厂方法，从 Map 创建状态
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
     * 返回当前状态的可变副本，支持链式写入
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
