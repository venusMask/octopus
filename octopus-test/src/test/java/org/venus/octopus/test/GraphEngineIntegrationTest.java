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
        // 构建图（模拟业务包含：判定、工作节点、以及结束路由）
        StateGraph<MapAgentState> graphBuilder = new StateGraph<>(MapAgentState::new);

        // 假装这是调用 LLM 的核心节点
        graphBuilder.addNode("llm_agent", state -> {
            int rounds = state.containsKey("rounds") ? (int) state.get("rounds") : 0;
            state.put("rounds", rounds + 1);
            state.put("next_route", rounds >= 2 ? "end" : "mock_tool");
            return state;
        });

        // 假装这是执行外部工具的节点
        graphBuilder.addNode("mock_tool", state -> {
            state.put("tool_result", "executed successfully");
            return state;
        });

        // 装配边
        graphBuilder.addEdge(Graph.START, "llm_agent");

        // 装配条件路由：如果 LLM 说去 tool 就去，否则去 END
        graphBuilder.addConditionalEdges("llm_agent", state -> (String) state.get("next_route"),
                Map.of("mock_tool", "mock_tool", "end", Graph.END));

        // 工具结束后必定切回 LLM 再次评估
        graphBuilder.addEdge("mock_tool", "llm_agent");

        // 编译并启动状态机验证
        CompiledGraph<MapAgentState> compiledGraph = graphBuilder.compile();
        MapAgentState initialState = new MapAgentState();
        initialState.put("rounds", 0);

        MapAgentState resultState = compiledGraph.invoke(initialState);

        // 验证最后结果
        assertNotNull(resultState);
        assertEquals(3, (Integer) resultState.get("rounds"),
                "Agent should have been called 3 times (0->1, 1->2, 2->3)");
        assertEquals("executed successfully", resultState.get("tool_result"));
        assertEquals("end", resultState.get("next_route"));
    }
}
