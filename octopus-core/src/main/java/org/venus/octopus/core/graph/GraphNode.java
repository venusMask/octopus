package org.venus.octopus.core.graph;

import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.Node;

/**
 * 图的节点内部包装类
 * <p>
 * 持有节点名称和执行逻辑。
 * </p>
 *
 * @param <S>
 *            AgentState 类型
 */
public class GraphNode<S extends AgentState> {

    private final String name;
    private final Node<S> action;

    public GraphNode(String name, Node<S> action) {
        this.name = name;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public S execute(S state) {
        return action.process(state);
    }
}
