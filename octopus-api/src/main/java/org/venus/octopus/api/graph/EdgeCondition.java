package org.venus.octopus.api.graph;

import org.venus.octopus.api.agent.AgentState;

/**
 * Edge condition judgment interface (routing function).
 * <p>
 * Used for routing decisions of Conditional Edges. Returns the name of the next
 * node based on the current state, based on which the framework decides the
 * execution path.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 *
 * <pre>{@code
 * EdgeCondition<MapAgentState> condition = state -> {
 *     AiMessage aiMsg = state.get("last_message");
 *     return aiMsg.hasToolCalls() ? "tools" : "end";
 * };
 * }</pre>
 *
 * @param <S>
 *            The concrete type of AgentState
 */
@FunctionalInterface
public interface EdgeCondition<S extends AgentState> {

    /**
     * Decides the next node name based on the current state.
     *
     * @param state
     *            The current Agent state
     * @return The name of the next node (should match a mapping key in
     *         addConditionalEdges, or be {@link Graph#END})
     */
    String route(S state);
}
