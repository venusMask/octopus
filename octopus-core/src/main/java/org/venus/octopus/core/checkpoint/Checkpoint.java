package org.venus.octopus.core.checkpoint;

import java.util.*;
import org.venus.octopus.api.agent.AgentState;

/**
 * Checkpoint interface.
 * <p>
 * Checkpoints save state snapshots after each node's execution, supporting
 * recovery from historical checkpoints (breakpoint resume). Different threadIds
 * correspond to execution contexts for different users/sessions, isolated from
 * each other.
 * </p>
 *
 * @param <S>
 *            AgentState type
 */
public interface Checkpoint<S extends AgentState> {

    /**
     * Saves a checkpoint.
     *
     * @param threadId
     *            Thread/Session ID
     * @param nodeName
     *            The name of the node that just finished execution
     * @param state
     *            The state after node execution
     */
    void save(String threadId, String nodeName, S state);

    /**
     * Gets the latest checkpoint state.
     *
     * @param threadId
     *            Thread/Session ID
     * @return The latest state, or empty if none
     */
    Optional<S> getLatest(String threadId);

    /**
     * Gets a list of all checkpoints for a specified thread (in saved order).
     *
     * @param threadId
     *            Thread/Session ID
     * @return Checkpoint snapshot list
     */
    List<CheckpointEntry<S>> getHistory(String threadId);

    /**
     * Clears all checkpoints for a specified thread.
     *
     * @param threadId
     *            Thread/Session ID
     */
    void clear(String threadId);

    /**
     * Single checkpoint record.
     *
     * @param nodeName
     *            Node name
     * @param state
     *            State snapshot
     * @param timestamp
     *            Save timestamp (milliseconds)
     */
    record CheckpointEntry<S extends AgentState>(String nodeName, S state, long timestamp) {
    }
}
