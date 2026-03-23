package org.venus.octopus.core.graph;

import java.util.List;
import java.util.stream.Stream;
import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.CompiledGraph;
import org.venus.octopus.core.runner.GraphRunner;

/**
 * Concrete implementation of a compiled graph.
 * <p>
 * Holds the graph structure definition {@link GraphDefinition} and the runner
 * {@link GraphRunner}, providing two execution modes: {@link #invoke} and
 * {@link #stream}.
 * </p>
 *
 * @param <S>
 *            AgentState type
 */
public class CompiledGraphImpl<S extends AgentState> implements CompiledGraph<S> {

    private final GraphDefinition<S> definition;
    private final GraphRunner<S> runner;

    public CompiledGraphImpl(GraphDefinition<S> definition, GraphRunner<S> runner) {
        this.definition = definition;
        this.runner = runner;
    }

    /**
     * Executes the graph synchronously, blocking until completion.
     *
     * @param initialState
     *            Initial state
     * @return Final state
     */
    @Override
    public S invoke(S initialState) {
        return runner.run(initialState);
    }

    /**
     * Executes the graph in a streaming fashion, returning intermediate states node
     * by node.
     *
     * @param initialState
     *            Initial state
     * @return Node output stream
     */
    @Override
    public Stream<NodeOutput<S>> stream(S initialState) {
        List<NodeOutput<S>> outputs = runner.runWithOutputs(initialState);
        return outputs.stream();
    }
}
