package org.venus.octopus.core.checkpoint;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.venus.octopus.api.agent.AgentState;

/**
 * In-memory checkpoint implementation.
 * <p>
 * Saves all checkpoints in memory, suitable for development and testing
 * scenarios. Data is lost after process restart; if persistence is required,
 * implement the {@link Checkpoint} interface and connect to a database.
 * </p>
 * <p>
 * This implementation is thread-safe.
 * </p>
 *
 * @param <S>
 *            AgentState type
 */
public class InMemoryCheckpoint<S extends AgentState> implements Checkpoint<S> {

    /** key: threadId, value: The checkpoint history list for this thread */
    private final Map<String, List<CheckpointEntry<S>>> store = new ConcurrentHashMap<>();

    @Override
    public void save(String threadId, String nodeName, S state) {
        CheckpointEntry<S> entry = new CheckpointEntry<>(nodeName, state, System.currentTimeMillis());
        store.computeIfAbsent(threadId, k -> new ArrayList<>()).add(entry);
    }

    @Override
    public Optional<S> getLatest(String threadId) {
        List<CheckpointEntry<S>> entries = store.get(threadId);
        if (entries == null || entries.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(entries.get(entries.size() - 1).state());
    }

    @Override
    public List<CheckpointEntry<S>> getHistory(String threadId) {
        List<CheckpointEntry<S>> entries = store.get(threadId);
        if (entries == null) {
            return List.of();
        }
        return Collections.unmodifiableList(entries);
    }

    @Override
    public void clear(String threadId) {
        store.remove(threadId);
    }

    /**
     * Gets a list of all threadIds.
     */
    public Set<String> getAllThreadIds() {
        return Collections.unmodifiableSet(store.keySet());
    }
}
