package org.venus.octopus.core.graph;

import java.util.Map;
import java.util.function.Supplier;
import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.*;
import org.venus.octopus.common.exception.GraphException;
import org.venus.octopus.common.utils.AssertUtils;
import org.venus.octopus.core.runner.GraphRunner;

/**
 * Graph builder (core entry point API).
 * <p>
 * Analogous to LangGraph's {@code StateGraph}, provides a fluent API for
 * defining graph nodes and edges, finally generating an executable
 * {@link CompiledGraph} via {@link #compile()}.
 * </p>
 *
 * <p>
 * Typical usage:
 * </p>
 * 
 * <pre>{@code
 * CompiledGraph<MapAgentState> app = new StateGraph<>(MapAgentState::new).addNode("agent", state -> agentNode(state))
 *         .addNode("tools", state -> toolsNode(state)).addEdge(Graph.START, "agent").addConditionalEdges("agent",
 *                 state -> shouldContinue(state) ? "tools" : "end", Map.of("tools", "tools", "end", Graph.END))
 *         .addEdge("tools", "agent").compile();
 *
 * MapAgentState result = app.invoke(initialState);
 * }</pre>
 *
 * @param <S>
 *            The specific type of AgentState
 */
public class StateGraph<S extends AgentState> implements GraphBuilder<S> {

    /** Maximum recursion (loop) count to prevent infinite loops */
    private static final int DEFAULT_MAX_ITERATIONS = 100;

    private final GraphDefinition<S> definition;
    private final Supplier<S> stateFactory;
    private int maxIterations = DEFAULT_MAX_ITERATIONS;

    /**
     * Creates a StateGraph.
     *
     * @param stateFactory
     *            State factory function, used to create initial state copies
     */
    public StateGraph(Supplier<S> stateFactory) {
        AssertUtils.notNull(stateFactory, "stateFactory cannot be null");
        this.stateFactory = stateFactory;
        this.definition = new GraphDefinition<>();
    }

    /**
     * Sets the maximum number of iterations (to prevent infinite loops in the
     * graph).
     *
     * @param maxIterations
     *            Maximum count, defaults to 100
     * @return The current builder
     */
    public StateGraph<S> withMaxIterations(int maxIterations) {
        AssertUtils.isTrue(maxIterations > 0, "maxIterations must be greater than 0");
        this.maxIterations = maxIterations;
        return this;
    }

    @Override
    public StateGraph<S> addNode(String name, Node<S> node) {
        AssertUtils.notEmpty(name, "Node name cannot be empty");
        AssertUtils.notNull(node, "Node logic cannot be null");
        if (Graph.START.equals(name) || Graph.END.equals(name)) {
            throw new GraphException("'" + name + "' is a reserved node name and cannot be used for a normal node");
        }
        definition.registerNode(new GraphNode<>(name, node));
        return this;
    }

    @Override
    public StateGraph<S> addEdge(String source, String target) {
        AssertUtils.notEmpty(source, "Edge source cannot be empty");
        AssertUtils.notEmpty(target, "Edge target cannot be empty");
        definition.registerEdge(GraphEdge.direct(source, target));
        return this;
    }

    @Override
    public StateGraph<S> addConditionalEdges(String source, EdgeCondition<S> condition, Map<String, String> pathMap) {
        AssertUtils.notEmpty(source, "Conditional edge source cannot be empty");
        AssertUtils.notNull(condition, "Routing function cannot be null");
        AssertUtils.notEmpty(pathMap, "Path map cannot be empty");
        definition.registerEdge(GraphEdge.conditional(source, condition, pathMap));
        return this;
    }

    @Override
    public CompiledGraph<S> compile() {
        definition.validate();
        GraphRunner<S> runner = new GraphRunner<>(definition, maxIterations);
        return new CompiledGraphImpl<>(definition, runner);
    }
}
