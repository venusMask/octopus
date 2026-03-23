package org.venus.octopus.api.graph;

import java.util.stream.Stream;
import org.venus.octopus.api.agent.AgentState;

/**
 * 已编译图接口
 * <p>
 * 通过 {@link GraphBuilder#compile()} 生成，持有图的完整结构定义和运行器。 提供两种执行模式：
 * <ul>
 * <li>{@link #invoke} — 同步执行，阻塞直到图运行结束，返回最终状态</li>
 * <li>{@link #stream} — 流式执行，逐节点返回中间状态，适合实时观测</li>
 * </ul>
 * </p>
 *
 * @param <S>
 *            AgentState 的具体类型
 */
public interface CompiledGraph<S extends AgentState> {

    /**
     * 同步执行图，返回最终状态
     *
     * @param initialState
     *            初始状态
     * @return 图执行完毕后的最终状态
     * @throws org.venus.octopus.common.exception.GraphException
     *             若执行过程中发生错误
     */
    S invoke(S initialState);

    /**
     * 流式执行图，逐节点返回中间状态
     * <p>
     * 每个 Stream 元素代表执行完一个节点后的状态快照。
     * </p>
     *
     * @param initialState
     *            初始状态
     * @return 节点状态流
     */
    Stream<NodeOutput<S>> stream(S initialState);

    /**
     * 节点输出，包含节点名称和执行完该节点后的状态
     *
     * @param nodeName
     *            当前执行的节点名称
     * @param state
     *            节点执行后的状态
     * @param <S>
     *            AgentState 类型
     */
    record NodeOutput<S extends AgentState>(String nodeName, S state) {
    }
}
