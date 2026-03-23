package org.venus.octopus.api.graph;

import org.venus.octopus.api.agent.AgentState;

/**
 * 节点接口
 * <p>
 * 节点是图流程中的基本执行单元。每个节点接收当前 AgentState，
 * 执行业务逻辑后返回更新后的状态（或状态片段）。
 * </p>
 *
 * <p>建议使用函数式写法（如 Lambda 表达式）通过 {@link NodeAction} 定义简单节点。</p>
 *
 * @param <S> AgentState 的具体类型
 */
@FunctionalInterface
public interface Node<S extends AgentState> {

    /**
     * 执行节点逻辑
     *
     * @param state 当前 Agent 状态
     * @return 更新后的状态（通常为包含变更项的新状态或原状态的副本）
     */
    S process(S state);
}
