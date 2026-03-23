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
 * 图运行引擎
 * <p>
 * 实现图的状态机循环执行逻辑：
 * <pre>
 *   START → node_A → node_B → ... → END
 *                   ↑       ↓ (条件边)
 *                   ← node_C
 * </pre>
 *
 * 执行算法：
 * <ol>
 *   <li>从入口边（START 出发的边）确定第一个节点</li>
 *   <li>执行当前节点，获取更新后的状态</li>
 *   <li>根据当前节点的出边（直接边或条件边）确定下一个节点</li>
 *   <li>若下一个节点为 END，停止循环并返回最终状态</li>
 *   <li>若迭代次数超过上限，抛出异常防止无限循环</li>
 * </ol>
 * </p>
 *
 * @param <S> AgentState 类型
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
     * 同步执行图，返回最终状态
     *
     * @param initialState 初始状态
     * @return 最终状态
     */
    public S run(S initialState) {
        List<CompiledGraph.NodeOutput<S>> outputs = runWithOutputs(initialState);
        if (outputs.isEmpty()) {
            return initialState;
        }
        return outputs.get(outputs.size() - 1).state();
    }

    /**
     * 执行图，返回所有节点的输出列表（用于流式场景）
     *
     * @param initialState 初始状态
     * @return 各节点输出列表
     */
    public List<CompiledGraph.NodeOutput<S>> runWithOutputs(S initialState) {
        List<CompiledGraph.NodeOutput<S>> outputs = new ArrayList<>();
        S currentState = initialState;

        // 从 START 出发，找到第一个节点名称
        String currentNodeName = resolveNext(Graph.START, currentState);

        int iteration = 0;
        while (!Graph.END.equals(currentNodeName)) {
            if (iteration++ >= maxIterations) {
                throw new GraphException("图执行超过最大迭代次数 " + maxIterations + "，可能存在无限循环。"
                        + "如需更多迭代，请调用 StateGraph.withMaxIterations(n) 设置更大的值。");
            }

            // 获取并执行当前节点
            GraphNode<S> node = definition.getNode(currentNodeName);
            if (node == null) {
                throw new GraphException("节点 '" + currentNodeName + "' 未注册");
            }

            log.debug("执行节点: {}, 当前状态: {}", currentNodeName, currentState);

            try {
                currentState = node.execute(currentState);
            } catch (Exception e) {
                throw new NodeException(currentNodeName, "节点执行失败: " + e.getMessage(), e);
            }

            outputs.add(new CompiledGraph.NodeOutput<>(currentNodeName, currentState));
            log.debug("节点 {} 执行完毕，更新后状态: {}", currentNodeName, currentState);

            // 解析下一个节点
            currentNodeName = resolveNext(currentNodeName, currentState);
        }

        log.debug("图执行完毕，共执行 {} 个节点", iteration);
        return outputs;
    }

    /**
     * 根据当前节点名称和状态，解析下一个节点名称
     */
    private String resolveNext(String currentNodeName, S currentState) {
        List<GraphEdge<S>> edges = definition.getEdgesFrom(currentNodeName);
        if (edges.isEmpty()) {
            throw new GraphException(
                    "节点 '" + currentNodeName + "' 没有定义出边，图执行无法继续。" + "请检查是否遗漏了 addEdge 或 addConditionalEdges 调用。");
        }
        // 目前每个节点只支持一条出边（直接边或条件边）
        GraphEdge<S> edge = edges.get(0);
        return edge.resolveNext(currentState);
    }
}
