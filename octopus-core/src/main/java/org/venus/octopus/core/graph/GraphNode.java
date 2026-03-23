package org.venus.octopus.core.graph;

import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.Node;

/**
 * Internal node wrapper class for the graph.
 * <p>
 * Holds the node name and execution logic.
 * </p>
 *
 * @param <S>
 *            AgentState type
 */
public class GraphNode<S extends AgentState> {

    private final String name;
    private final Node<S> action;

    public GraphNode(String name, Node<S> action) {
        this.name = name;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public S execute(S state) {
        return action.process(state);
    }
}
