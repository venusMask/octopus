package org.venus.octopus.api.graph;

import org.venus.octopus.api.agent.AgentState;

import java.util.Map;

/**
 * 图构建器接口
 * <p>
 * 提供流式 API 来定义图的结构（节点和边）。构建完毕后通过 {@link #compile()} 生成可执行图。
 * </p>
 *
 * @param <S> AgentState 的具体类型
 */
public interface GraphBuilder<S extends AgentState> {

    /**
     * 添加节点
     *
     * @param name 节点名称（必须唯一）
     * @param node 节点执行逻辑
     * @return 当前构建器（支持链式调用）
     */
    GraphBuilder<S> addNode(String name, Node<S> node);

    /**
     * 添加直接边（从 source 节点执行完毕后，直接跳转到 target 节点）
     *
     * @param source 源节点名称（可使用 {@link Graph#START}）
     * @param target 目标节点名称（可使用 {@link Graph#END}）
     * @return 当前构建器（支持链式调用）
     */
    GraphBuilder<S> addEdge(String source, String target);

    /**
     * 添加条件边（从 source 节点执行完毕后，根据路由函数的返回值决定下一个节点）
     *
     * @param source        源节点名称
     * @param condition     路由函数，返回路由键（pathMap 中的 key）
     * @param pathMap       路由映射，key 为路由函数的返回值，value 为目标节点名称
     * @return 当前构建器（支持链式调用）
     */
    GraphBuilder<S> addConditionalEdges(String source, EdgeCondition<S> condition, Map<String, String> pathMap);

    /**
     * 设置入口节点（等价于 addEdge(Graph.START, entryPoint)）
     *
     * @param entryPoint 入口节点名称
     * @return 当前构建器（支持链式调用）
     */
    default GraphBuilder<S> setEntryPoint(String entryPoint) {
        return addEdge(Graph.START, entryPoint);
    }

    /**
     * 设置终止节点（等价于 addEdge(finishPoint, Graph.END)）
     *
     * @param finishPoint 终止节点名称
     * @return 当前构建器（支持链式调用）
     */
    default GraphBuilder<S> setFinishPoint(String finishPoint) {
        return addEdge(finishPoint, Graph.END);
    }

    /**
     * 编译图，生成可执行的 {@link CompiledGraph}
     *
     * @return 已编译的图
     * @throws org.venus.octopus.common.exception.GraphException 若图结构不完整或存在错误
     */
    CompiledGraph<S> compile();
}
