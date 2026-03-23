package org.venus.octopus.api.graph;

import java.util.stream.Stream;
import org.venus.octopus.api.agent.AgentState;

/**
 * Compiled graph interface.
 * <p>
 * Generated via {@link GraphBuilder#compile()}, holding the complete graph
 * structure definition and runner. Provides two execution modes:
 * <ul>
 * <li>{@link #invoke} — Synchronous execution, blocking until the graph
 * finishes, returning the final state</li>
 * <li>{@link #stream} — Streaming execution, returning intermediate states
 * node-by-node, suitable for real-time observation</li>
 * </ul>
 * </p>
 *
 * @param <S>
 *            The concrete type of AgentState
 */
public interface CompiledGraph<S extends AgentState> {

    /**
     * Synchronously executes the graph, returning the final state.
     *
     * @param initialState
     *            The initial state
     * @return The final state after graph execution finishes
     * @throws org.venus.octopus.common.exception.GraphException
     *             If an error occurs during execution
     */
    S invoke(S initialState);

    /**
     * Stream executes the graph, returning intermediate states node-by-node.
     * <p>
     * Each Stream element represents a state snapshot after a node is executed.
     * </p>
     *
     * @param initialState
     *            The initial state
     * @return A stream of node outputs
     */
    Stream<NodeOutput<S>> stream(S initialState);

    /**
     * Node output, containing the node name and the state after executing that
     * node.
     *
     * @param nodeName
     *            The name of the currently executed node
     * @param state
     *            The state after node execution
     * @param <S>
     *            The AgentState type
     */
    record NodeOutput<S extends AgentState>(String nodeName, S state) {
    }
}
