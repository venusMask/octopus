package org.venus.octopus.core.graph;

import java.util.Map;
import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.EdgeCondition;

/**
 * Graph edge description class.
 * <p>
 * Distinguishes between direct edges (Direct) and conditional edges
 * (Conditional).
 * </p>
 *
 * @param <S>
 *            AgentState type
 */
public class GraphEdge<S extends AgentState> {

    /** Edge type */
    public enum EdgeType {
        /** Direct edge: source -> target */
        DIRECT,
        /** Conditional edge: source -> routing function -> target */
        CONDITIONAL
    }

    private final String source;
    private final EdgeType type;

    // Direct edge
    private final String target;

    // Conditional edge
    private final EdgeCondition<S> condition;
    private final Map<String, String> pathMap;

    /** Constructs a direct edge */
    public static <S extends AgentState> GraphEdge<S> direct(String source, String target) {
        return new GraphEdge<>(source, target);
    }

    /** Constructs a conditional edge */
    public static <S extends AgentState> GraphEdge<S> conditional(String source, EdgeCondition<S> condition,
            Map<String, String> pathMap) {
        return new GraphEdge<>(source, condition, pathMap);
    }

    private GraphEdge(String source, String target) {
        this.source = source;
        this.type = EdgeType.DIRECT;
        this.target = target;
        this.condition = null;
        this.pathMap = null;
    }

    private GraphEdge(String source, EdgeCondition<S> condition, Map<String, String> pathMap) {
        this.source = source;
        this.type = EdgeType.CONDITIONAL;
        this.target = null;
        this.condition = condition;
        this.pathMap = Map.copyOf(pathMap);
    }

    public String getSource() {
        return source;
    }

    public EdgeType getType() {
        return type;
    }

    public boolean isDirect() {
        return type == EdgeType.DIRECT;
    }

    public boolean isConditional() {
        return type == EdgeType.CONDITIONAL;
    }

    /**
     * Resolves the next node name based on the current state.
     *
     * @param state
     *            Current state
     * @return Next node name
     */
    public String resolveNext(S state) {
        if (isDirect()) {
            return target;
        }
        // Conditional edge: calls the routing function, then looks up the target node
        // in the pathMap
        String routeKey = condition.route(state);
        String next = pathMap.get(routeKey);
        if (next == null) {
            throw new IllegalStateException("Conditional edge routing key '" + routeKey
                    + "' was not found in the path map; available keys: " + pathMap.keySet());
        }
        return next;
    }
}
