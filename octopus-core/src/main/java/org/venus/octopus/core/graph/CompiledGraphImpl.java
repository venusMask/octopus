package org.venus.octopus.core.graph;

import java.util.List;
import java.util.stream.Stream;
import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.CompiledGraph;
import org.venus.octopus.core.runner.GraphRunner;

/**
 * 已编译图的具体实现
 * <p>
 * 持有图结构定义 {@link GraphDefinition} 和运行器 {@link GraphRunner}，
 * 提供 {@link #invoke} 和 {@link #stream} 两种执行模式。
 * </p>
 *
 * @param <S> AgentState 类型
 */
public class CompiledGraphImpl<S extends AgentState> implements CompiledGraph<S> {

    private final GraphDefinition<S> definition;
    private final GraphRunner<S> runner;

    public CompiledGraphImpl(GraphDefinition<S> definition, GraphRunner<S> runner) {
        this.definition = definition;
        this.runner = runner;
    }

    /**
     * 同步执行图，阻塞直到结束
     *
     * @param initialState 初始状态
     * @return 最终状态
     */
    @Override
    public S invoke(S initialState) {
        return runner.run(initialState);
    }

    /**
     * 流式执行图，逐节点返回中间状态
     *
     * @param initialState 初始状态
     * @return 节点输出流
     */
    @Override
    public Stream<NodeOutput<S>> stream(S initialState) {
        List<NodeOutput<S>> outputs = runner.runWithOutputs(initialState);
        return outputs.stream();
    }
}
