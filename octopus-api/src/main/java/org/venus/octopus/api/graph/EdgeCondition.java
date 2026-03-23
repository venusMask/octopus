package org.venus.octopus.api.graph;

import org.venus.octopus.api.agent.AgentState;

/**
 * 边条件判断接口（路由函数）
 * <p>
 * 用于条件边（Conditional Edge）的路由决策。根据当前状态返回下一个节点的名称， 框架据此决定执行哪条路径。
 * </p>
 *
 * <p>
 * 使用示例：
 * </p>
 * 
 * <pre>{@code
 * EdgeCondition<MapAgentState> condition = state -> {
 *     AiMessage aiMsg = state.get("last_message");
 *     return aiMsg.hasToolCalls() ? "tools" : "end";
 * };
 * }</pre>
 *
 * @param <S>
 *            AgentState 的具体类型
 */
@FunctionalInterface
public interface EdgeCondition<S extends AgentState> {

    /**
     * 根据当前状态决定下一个节点名称
     *
     * @param state
     *            当前 Agent 状态
     * @return 下一个节点的名称（应与 addConditionalEdges 中的映射键匹配，或为 {@link Graph#END}）
     */
    String route(S state);
}
