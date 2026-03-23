package org.venus.octopus.core.graph;

import java.util.*;
import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.Graph;
import org.venus.octopus.common.exception.GraphException;

/**
 * 图结构定义（图的只读结构描述）
 * <p>
 * 存储图的节点表和边表，是 StateGraph 构建后的中间产物， 由 {@link StateGraph#compile()} 生成后交给
 * CompiledGraphImpl 使用。
 * </p>
 *
 * @param <S>
 *            AgentState 类型
 */
public class GraphDefinition<S extends AgentState> {

    /** 节点表：节点名称 -> 节点包装 */
    private final Map<String, GraphNode<S>> nodes;

    /** 边表：源节点名称 -> 边列表（一个源节点可能有多条边，但直接边只有一条） */
    private final Map<String, List<GraphEdge<S>>> edges;

    /** 入口节点名称（从 START 出发的第一个节点） */
    private String entryPoint;

    public GraphDefinition() {
        this.nodes = new LinkedHashMap<>();
        this.edges = new LinkedHashMap<>();
    }

    /**
     * 注册节点（仅在图构建阶段调用）
     */
    public void registerNode(GraphNode<S> node) {
        String name = node.getName();
        if (nodes.containsKey(name)) {
            throw new GraphException("节点 '" + name + "' 已存在，节点名称必须唯一");
        }
        nodes.put(name, node);
    }

    /**
     * 注册边（仅在图构建阶段调用）
     */
    public void registerEdge(GraphEdge<S> edge) {
        String source = edge.getSource();
        edges.computeIfAbsent(source, k -> new ArrayList<>()).add(edge);

        // 识别入口节点
        if (Graph.START.equals(source) && edge.isDirect()) {
            // 直接通过 addEdge(START, xxx) 设置入口
        }
    }

    /**
     * 验证图结构完整性
     */
    public void validate() {
        List<GraphEdge<S>> startEdges = edges.get(Graph.START);
        if (startEdges == null || startEdges.isEmpty()) {
            throw new GraphException("图缺少入口边，请调用 addEdge(Graph.START, firstNode) 设置入口");
        }

        // 验证所有边引用的节点都已注册
        for (Map.Entry<String, List<GraphEdge<S>>> entry : edges.entrySet()) {
            String source = entry.getKey();
            if (!Graph.START.equals(source) && !nodes.containsKey(source)) {
                throw new GraphException("边的源节点 '" + source + "' 未注册");
            }
        }
    }

    public Map<String, GraphNode<S>> getNodes() {
        return Collections.unmodifiableMap(nodes);
    }

    public Map<String, List<GraphEdge<S>>> getEdges() {
        return Collections.unmodifiableMap(edges);
    }

    /**
     * 获取指定节点的下一条边
     *
     * @param nodeName
     *            节点名称
     * @return 边列表（可能为空或只有一条）
     */
    public List<GraphEdge<S>> getEdgesFrom(String nodeName) {
        return edges.getOrDefault(nodeName, List.of());
    }

    /**
     * 根据名称获取节点
     */
    public GraphNode<S> getNode(String name) {
        return nodes.get(name);
    }
}
