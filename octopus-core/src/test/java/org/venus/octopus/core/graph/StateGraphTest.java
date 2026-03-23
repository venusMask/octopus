package org.venus.octopus.core.graph;

import org.junit.jupiter.api.Test;
import org.venus.octopus.api.graph.CompiledGraph;
import org.venus.octopus.api.graph.Graph;
import org.venus.octopus.api.message.HumanMessage;
import org.venus.octopus.common.exception.GraphException;
import org.venus.octopus.core.state.MapAgentState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StateGraph 核心功能单元测试
 */
class StateGraphTest {

    // -----------------------------------------------------------------------
    // 1. 基础图：单节点，START -> node -> END
    // -----------------------------------------------------------------------

    @Test
    void testSingleNode_shouldExecuteAndReturnState() {
        CompiledGraph<MapAgentState> app = new StateGraph<>(MapAgentState::new)
                .addNode("greet", state -> {
                    state.put("result", "hello " + state.get("name"));
                    return state;
                })
                .addEdge(Graph.START, "greet")
                .addEdge("greet", Graph.END)
                .compile();

        MapAgentState initial = MapAgentState.of("name", "octopus");
        MapAgentState result = app.invoke(initial);

        assertEquals("hello octopus", result.get("result"));
    }

    // -----------------------------------------------------------------------
    // 2. 多节点链：START -> A -> B -> END
    // -----------------------------------------------------------------------

    @Test
    void testMultiNodeChain_shouldExecuteInOrder() {
        List<String> executionOrder = new ArrayList<>();

        CompiledGraph<MapAgentState> app = new StateGraph<>(MapAgentState::new)
                .addNode("step1", state -> {
                    executionOrder.add("step1");
                    state.put("value", 1);
                    return state;
                })
                .addNode("step2", state -> {
                    executionOrder.add("step2");
                    int v = (int) state.get("value");
                    state.put("value", v + 10);
                    return state;
                })
                .addEdge(Graph.START, "step1")
                .addEdge("step1", "step2")
                .addEdge("step2", Graph.END)
                .compile();

        MapAgentState result = app.invoke(new MapAgentState());

        assertEquals(List.of("step1", "step2"), executionOrder);
        assertEquals(11, (int) result.get("value"));
    }

    // -----------------------------------------------------------------------
    // 3. 条件边：根据状态路由不同路径
    // -----------------------------------------------------------------------

    @Test
    void testConditionalEdge_shouldRouteToCorrectNode() {
        CompiledGraph<MapAgentState> app = new StateGraph<>(MapAgentState::new)
                .addNode("router", state -> state)  // 不修改状态
                .addNode("pathA", state -> {
                    state.put("path", "A");
                    return state;
                })
                .addNode("pathB", state -> {
                    state.put("path", "B");
                    return state;
                })
                .addEdge(Graph.START, "router")
                .addConditionalEdges("router",
                        state -> (String) state.get("direction"),
                        Map.of("A", "pathA", "B", "pathB"))
                .addEdge("pathA", Graph.END)
                .addEdge("pathB", Graph.END)
                .compile();

        // 路由到 pathA
        MapAgentState stateA = MapAgentState.of("direction", "A");
        assertEquals("A", app.invoke(stateA).get("path"));

        // 路由到 pathB
        MapAgentState stateB = MapAgentState.of("direction", "B");
        assertEquals("B", app.invoke(stateB).get("path"));
    }

    // -----------------------------------------------------------------------
    // 4. 流式执行：验证每个节点都有输出
    // -----------------------------------------------------------------------

    @Test
    void testStream_shouldReturnOutputForEachNode() {
        CompiledGraph<MapAgentState> app = new StateGraph<>(MapAgentState::new)
                .addNode("nodeA", state -> { state.put("a", 1); return state; })
                .addNode("nodeB", state -> { state.put("b", 2); return state; })
                .addEdge(Graph.START, "nodeA")
                .addEdge("nodeA", "nodeB")
                .addEdge("nodeB", Graph.END)
                .compile();

        List<String> nodeNames = app.stream(new MapAgentState())
                .map(CompiledGraph.NodeOutput::nodeName)
                .collect(Collectors.toList());

        assertEquals(List.of("nodeA", "nodeB"), nodeNames);
    }

    // -----------------------------------------------------------------------
    // 5. 循环图：Agent <-> Tools（模拟 ReAct 两步后结束）
    // -----------------------------------------------------------------------

    @Test
    void testCyclicGraph_reactPattern() {
        // 模拟：agent决定是否用工具；工具用完回到agent；agent第二次决定结束
        CompiledGraph<MapAgentState> app = new StateGraph<>(MapAgentState::new)
                .addNode("agent", state -> {
                    int count = state.containsKey("count") ? (int) state.get("count") : 0;
                    state.put("count", count + 1);
                    // 第一次调工具，第二次结束
                    state.put("next", count >= 1 ? "end" : "tools");
                    return state;
                })
                .addNode("tools", state -> {
                    state.put("toolResult", "done");
                    return state;
                })
                .addEdge(Graph.START, "agent")
                .addConditionalEdges("agent",
                        state -> (String) state.get("next"),
                        Map.of("tools", "tools", "end", Graph.END))
                .addEdge("tools", "agent")
                .compile();

        MapAgentState result = app.invoke(new MapAgentState());

        assertEquals(2, (int) result.get("count"));
        assertEquals("done", result.get("toolResult"));
    }

    // -----------------------------------------------------------------------
    // 6. 异常场景：缺少入口边
    // -----------------------------------------------------------------------

    @Test
    void testMissingEntryEdge_shouldThrowGraphException() {
        StateGraph<MapAgentState> graph = new StateGraph<>(MapAgentState::new)
                .addNode("node", state -> state);
        // 没有 addEdge(Graph.START, ...)

        assertThrows(GraphException.class, graph::compile);
    }

    // -----------------------------------------------------------------------
    // 7. 异常场景：重复节点名
    // -----------------------------------------------------------------------

    @Test
    void testDuplicateNodeName_shouldThrowGraphException() {
        StateGraph<MapAgentState> graph = new StateGraph<>(MapAgentState::new);
        graph.addNode("node", state -> state);

        assertThrows(GraphException.class, () -> graph.addNode("node", state -> state));
    }

    // -----------------------------------------------------------------------
    // 8. 超过最大迭代次数
    // -----------------------------------------------------------------------

    @Test
    void testMaxIterationsExceeded_shouldThrowGraphException() {
        // 构建一个无终止条件的循环图
        CompiledGraph<MapAgentState> app = new StateGraph<>(MapAgentState::new)
                .withMaxIterations(5)  // 最多 5 次
                .addNode("loop", state -> state)
                .addEdge(Graph.START, "loop")
                .addEdge("loop", "loop")  // 死循环
                .compile();

        assertThrows(GraphException.class, () -> app.invoke(new MapAgentState()));
    }
}
