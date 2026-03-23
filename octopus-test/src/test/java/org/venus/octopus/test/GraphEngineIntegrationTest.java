package org.venus.octopus.test;

import org.junit.jupiter.api.Test;
import org.venus.octopus.api.graph.CompiledGraph;
import org.venus.octopus.api.graph.Graph;
import org.venus.octopus.core.graph.StateGraph;
import org.venus.octopus.core.state.MapAgentState;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GraphEngineIntegrationTest {

    @Test
    void testBasicGraphExecutionCycle() throws Exception {
        // Build the graph (simulating business logic including decision, worker nodes,
        // and end routing)
        StateGraph<MapAgentState> graphBuilder = new StateGraph<>(MapAgentState::new);

        // Pretend this is the core node calling the LLM
        graphBuilder.addNode("llm_agent", state -> {
            int rounds = state.containsKey("rounds") ? (int) state.get("rounds") : 0;
            state.put("rounds", rounds + 1);
            state.put("next_route", rounds >= 2 ? "end" : "mock_tool");
            return state;
        });

        // Pretend this is the node executing an external tool
        graphBuilder.addNode("mock_tool", state -> {
            state.put("tool_result", "executed successfully");
            return state;
        });

        // Assemble edges
        graphBuilder.addEdge(Graph.START, "llm_agent");

        // Assemble conditional routing: go to tool if LLM says so, otherwise go to END
        graphBuilder.addConditionalEdges("llm_agent", state -> (String) state.get("next_route"),
                Map.of("mock_tool", "mock_tool", "end", Graph.END));

        // After the tool finishes, it must switch back to the LLM for re-evaluation
        graphBuilder.addEdge("mock_tool", "llm_agent");

        // Compile and start the state machine validation
        CompiledGraph<MapAgentState> compiledGraph = graphBuilder.compile();
        MapAgentState initialState = new MapAgentState();
        initialState.put("rounds", 0);

        MapAgentState resultState = compiledGraph.invoke(initialState);

        // Verify the final result
        assertNotNull(resultState);
        assertEquals(3, (Integer) resultState.get("rounds"),
                "Agent should have been called 3 times (0->1, 1->2, 2->3)");
        assertEquals("executed successfully", resultState.get("tool_result"));
        assertEquals("end", resultState.get("next_route"));
    }
}
