package org.venus.octopus.core.graph;

import java.util.Map;
import java.util.function.Supplier;
import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.*;
import org.venus.octopus.common.exception.GraphException;
import org.venus.octopus.common.utils.AssertUtils;
import org.venus.octopus.core.runner.GraphRunner;

/**
 * 图构建器（核心入口 API）
 * <p>
 * 类比 LangGraph 的 {@code StateGraph}，提供流式 API 来定义图的节点和边， 最终通过 {@link #compile()}
 * 生成可执行的 {@link CompiledGraph}。
 * </p>
 *
 * <p>
 * 典型用法：
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
 *            AgentState 的具体类型
 */
public class StateGraph<S extends AgentState> implements GraphBuilder<S> {

    /** 最大递归（循环）次数，防止无限循环 */
    private static final int DEFAULT_MAX_ITERATIONS = 100;

    private final GraphDefinition<S> definition;
    private final Supplier<S> stateFactory;
    private int maxIterations = DEFAULT_MAX_ITERATIONS;

    /**
     * 创建 StateGraph
     *
     * @param stateFactory
     *            状态工厂函数，用于创建初始状态副本
     */
    public StateGraph(Supplier<S> stateFactory) {
        AssertUtils.notNull(stateFactory, "stateFactory 不能为 null");
        this.stateFactory = stateFactory;
        this.definition = new GraphDefinition<>();
    }

    /**
     * 设置最大迭代次数（防止图无限循环）
     *
     * @param maxIterations
     *            最大次数，默认 100
     * @return 当前构建器
     */
    public StateGraph<S> withMaxIterations(int maxIterations) {
        AssertUtils.isTrue(maxIterations > 0, "maxIterations 必须大于 0");
        this.maxIterations = maxIterations;
        return this;
    }

    @Override
    public StateGraph<S> addNode(String name, Node<S> node) {
        AssertUtils.notEmpty(name, "节点名称不能为空");
        AssertUtils.notNull(node, "节点逻辑不能为 null");
        if (Graph.START.equals(name) || Graph.END.equals(name)) {
            throw new GraphException("'" + name + "' 是保留节点名称，不能作为普通节点");
        }
        definition.registerNode(new GraphNode<>(name, node));
        return this;
    }

    @Override
    public StateGraph<S> addEdge(String source, String target) {
        AssertUtils.notEmpty(source, "边的 source 不能为空");
        AssertUtils.notEmpty(target, "边的 target 不能为空");
        definition.registerEdge(GraphEdge.direct(source, target));
        return this;
    }

    @Override
    public StateGraph<S> addConditionalEdges(String source, EdgeCondition<S> condition, Map<String, String> pathMap) {
        AssertUtils.notEmpty(source, "条件边的 source 不能为空");
        AssertUtils.notNull(condition, "路由函数不能为 null");
        AssertUtils.notEmpty(pathMap, "路径映射不能为空");
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
