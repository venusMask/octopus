package org.venus.octopus.api.graph;

import java.util.Map;
import org.venus.octopus.api.agent.AgentState;

/**
 * Graph builder interface.
 * <p>
 * Provides a fluent API to define the graph structure (nodes and edges). Once
 * built, an executable graph is generated via {@link #compile()}.
 * </p>
 *
 * @param <S>
 *            The concrete type of AgentState
 */
public interface GraphBuilder<S extends AgentState> {

    /**
     * Adds a node.
     *
     * @param name
     *            The node name (must be unique)
     * @param node
     *            The node execution logic
     * @return The current builder (supports fluent chaining)
     */
    GraphBuilder<S> addNode(String name, Node<S> node);

    /**
     * Adds a direct edge (after source node execution, jump directly to target
     * node).
     *
     * @param source
     *            The source node name (can use {@link Graph#START})
     * @param target
     *            The target node name (can use {@link Graph#END})
     * @return The current builder (supports fluent chaining)
     */
    GraphBuilder<S> addEdge(String source, String target);

    /**
     * Adds a conditional edge (after source node execution, decide the next node
     * based on the routing function result).
     *
     * @param source
     *            The source node name
     * @param condition
     *            The routing function, returning a routing key (key in pathMap)
     * @param pathMap
     *            The routing map, where key is the return value of the routing
     *            function and value is the target node name
     * @return The current builder (supports fluent chaining)
     */
    GraphBuilder<S> addConditionalEdges(String source, EdgeCondition<S> condition, Map<String, String> pathMap);

    /**
     * Sets the entry point node (equivalent to addEdge(Graph.START, entryPoint)).
     *
     * @param entryPoint
     *            The entry point node name
     * @return The current builder (supports fluent chaining)
     */
    default GraphBuilder<S> setEntryPoint(String entryPoint) {
        return addEdge(Graph.START, entryPoint);
    }

    /**
     * Sets the finish point node (equivalent to addEdge(finishPoint, Graph.END)).
     *
     * @param finishPoint
     *            The finish point node name
     * @return The current builder (supports fluent chaining)
     */
    default GraphBuilder<S> setFinishPoint(String finishPoint) {
        return addEdge(finishPoint, Graph.END);
    }

    /**
     * Compiles the graph, generating an executable {@link CompiledGraph}.
     *
     * @return The compiled graph
     * @throws org.venus.octopus.common.exception.GraphException
     *             If the graph structure is incomplete or has errors
     */
    CompiledGraph<S> compile();
}
