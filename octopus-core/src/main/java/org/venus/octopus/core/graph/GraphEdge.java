package org.venus.octopus.core.graph;

import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.EdgeCondition;

import java.util.Map;

/**
 * 图的边描述类
 * <p>区分直接边（Direct）和条件边（Conditional）。</p>
 *
 * @param <S> AgentState 类型
 */
public class GraphEdge<S extends AgentState> {

    /** 边类型 */
    public enum EdgeType {
        /** 直接边：source -> target */
        DIRECT,
        /** 条件边：source -> 路由函数 -> target */
        CONDITIONAL
    }

    private final String source;
    private final EdgeType type;

    // 直接边
    private final String target;

    // 条件边
    private final EdgeCondition<S> condition;
    private final Map<String, String> pathMap;

    /** 构造直接边 */
    public static <S extends AgentState> GraphEdge<S> direct(String source, String target) {
        return new GraphEdge<>(source, target);
    }

    /** 构造条件边 */
    public static <S extends AgentState> GraphEdge<S> conditional(
            String source, EdgeCondition<S> condition, Map<String, String> pathMap) {
        return new GraphEdge<>(source, condition, pathMap);
    }

    private GraphEdge(String source, String target) {
        this.source = source;
        this.type = EdgeType.DIRECT;
        this.target = target;
        this.condition = null;
        this.pathMap = null;
    }

    private GraphEdge(String source, EdgeCondition<S> condition, Map<String, String> pathMap) {
        this.source = source;
        this.type = EdgeType.CONDITIONAL;
        this.target = null;
        this.condition = condition;
        this.pathMap = Map.copyOf(pathMap);
    }

    public String getSource() {
        return source;
    }

    public EdgeType getType() {
        return type;
    }

    public boolean isDirect() {
        return type == EdgeType.DIRECT;
    }

    public boolean isConditional() {
        return type == EdgeType.CONDITIONAL;
    }

    /**
     * 根据当前状态解析下一个节点名称
     *
     * @param state 当前状态
     * @return 下一个节点名称
     */
    public String resolveNext(S state) {
        if (isDirect()) {
            return target;
        }
        // 条件边：调用路由函数，再从 pathMap 中查找目标节点
        String routeKey = condition.route(state);
        String next = pathMap.get(routeKey);
        if (next == null) {
            throw new IllegalStateException(
                    "条件边路由键 '" + routeKey + "' 在路径映射中未找到，可用键: " + pathMap.keySet());
        }
        return next;
    }
}
