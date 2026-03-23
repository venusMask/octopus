package org.venus.octopus.api.graph;

/**
 * Graph constants and basic definitions interface.
 * <p>
 * Defines special node name constants in graph flow orchestration:
 * <ul>
 * <li>{@link #START} — The entry point node of the graph; all graph start edges
 * must originate from START</li>
 * <li>{@link #END} — The finish point node of the graph; graph execution
 * finishes upon reaching END</li>
 * </ul>
 * </p>
 */
public interface Graph {

    /**
     * The entry point node name of the graph.
     */
    String START = "__start__";

    /**
     * The finish point node name of the graph.
     */
    String END = "__end__";
}
