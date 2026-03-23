package org.venus.octopus.core.runner;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.CompiledGraph;
import org.venus.octopus.api.graph.Graph;
import org.venus.octopus.common.exception.GraphException;
import org.venus.octopus.common.exception.NodeException;
import org.venus.octopus.core.graph.GraphDefinition;
import org.venus.octopus.core.graph.GraphEdge;
import org.venus.octopus.core.graph.GraphNode;

/**
 * Graph execution engine.
 * <p>
 * Implements the state machine loop execution logic for the graph:
 * 
 * <pre>
 *   START → node_A → node_B → ... → END
 *                   ↑       ↓ (conditional edge)
 *                   ← node_C
 * </pre>
 *
 * Execution algorithm:
 * <ol>
 * <li>Determine the first node from the entry edge (the edge starting from
 * START)</li>
 * <li>Execute the current node and get the updated state</li>
 * <li>Determine the next node based on the current node's outgoing edge (direct
 * or conditional)</li>
 * <li>If the next node is END, stop the loop and return the final state</li>
 * <li>If the number of iterations exceeds the limit, throw an exception to
 * prevent an infinite loop</li>
 * </ol>
 * </p>
 *
 * @param <S>
 *            AgentState type
 */
public class GraphRunner<S extends AgentState> {

    private static final Logger log = LoggerFactory.getLogger(GraphRunner.class);

    private final GraphDefinition<S> definition;
    private final int maxIterations;

    public GraphRunner(GraphDefinition<S> definition, int maxIterations) {
        this.definition = definition;
        this.maxIterations = maxIterations;
    }

    /**
     * Executes the graph synchronously and returns the final state.
     *
     * @param initialState
     *            Initial state
     * @return Final state
     */
    public S run(S initialState) {
        List<CompiledGraph.NodeOutput<S>> outputs = runWithOutputs(initialState);
        if (outputs.isEmpty()) {
            return initialState;
        }
        return outputs.get(outputs.size() - 1).state();
    }

    /**
     * Executes the graph and returns a list of outputs for all nodes (for streaming
     * scenarios).
     *
     * @param initialState
     *            Initial state
     * @return List of outputs from each node
     */
    public List<CompiledGraph.NodeOutput<S>> runWithOutputs(S initialState) {
        List<CompiledGraph.NodeOutput<S>> outputs = new ArrayList<>();
        S currentState = initialState;

        // Starting from START, find the first node name
        String currentNodeName = resolveNext(Graph.START, currentState);

        int iteration = 0;
        while (!Graph.END.equals(currentNodeName)) {
            if (iteration++ >= maxIterations) {
                throw new GraphException("Graph execution exceeded maximum iterations " + maxIterations
                        + ", a possible infinite loop exists."
                        + " If more iterations are needed, call StateGraph.withMaxIterations(n) to set a larger value.");
            }

            // Get and execute current node
            GraphNode<S> node = definition.getNode(currentNodeName);
            if (node == null) {
                throw new GraphException("Node '" + currentNodeName + "' is not registered");
            }

            log.debug("Executing node: {}, current state: {}", currentNodeName, currentState);

            try {
                currentState = node.execute(currentState);
            } catch (Exception e) {
                throw new NodeException(currentNodeName, "Node execution failed: " + e.getMessage(), e);
            }

            outputs.add(new CompiledGraph.NodeOutput<>(currentNodeName, currentState));
            log.debug("Node {} execution completed, updated state: {}", currentNodeName, currentState);

            // Resolving next node
            currentNodeName = resolveNext(currentNodeName, currentState);
        }

        log.debug("Graph execution completed, total nodes executed: {}", iteration);
        return outputs;
    }

    /**
     * Resolves the next node name based on the current node name and state.
     */
    private String resolveNext(String currentNodeName, S currentState) {
        List<GraphEdge<S>> edges = definition.getEdgesFrom(currentNodeName);
        if (edges.isEmpty())
            throw new GraphException("Node '" + currentNodeName
                    + "' does not have an outgoing edge defined; graph execution cannot continue."
                    + " Please check if any addEdge or addConditionalEdges calls are missing.");
        // Currently, each node only supports one outgoing edge (direct or conditional)
        GraphEdge<S> edge = edges.get(0);
        return edge.resolveNext(currentState);
    }
}
