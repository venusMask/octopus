package org.venus.octopus.api.graph;

import org.venus.octopus.api.agent.AgentState;

/**
 * Node interface.
 * <p>
 * A node is the basic execution unit in a graph flow. Each node receives the
 * current AgentState, executes business logic, and returns the updated state
 * (or a state slice).
 * </p>
 *
 * <p>
 * Functional implementation (e.g., Lambda expressions) via {@link NodeAction}
 * is recommended for simple nodes.
 * </p>
 *
 * @param <S>
 *            The concrete type of AgentState
 */
@FunctionalInterface
public interface Node<S extends AgentState> {

    /**
     * Executes the node logic.
     *
     * @param state
     *            The current Agent state
     * @return The updated state (usually a new state containing changes or a copy
     *         of the original state)
     */
    S process(S state);
}
