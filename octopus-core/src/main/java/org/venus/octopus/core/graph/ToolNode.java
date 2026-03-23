package org.venus.octopus.core.graph;

import org.venus.octopus.api.agent.AgentState;
import org.venus.octopus.api.graph.Node;
import org.venus.octopus.api.message.AiMessage;
import org.venus.octopus.api.message.Message;
import org.venus.octopus.api.message.ToolMessage;
import org.venus.octopus.core.tool.ToolExecutor;
import org.venus.octopus.core.tool.ToolRegistry;
import org.venus.octopus.common.exception.NodeException;
import org.venus.octopus.common.utils.CollectionUtils;

import java.util.List;

/**
 * 包装了完全自主处理逻辑的内置工具节点
 * <p>
 * 它假定 AgentState 中包含一个名叫 "messages" 的键。 运行时从最后一条消息中读取内容，若判定为需要调用工具的
 * AiMessage，则将其转发给 {@link ToolExecutor}。 最终生成的 {@link ToolMessage} 列表将被封存在一个全新的
 * Map 中返回。
 * </p>
 */
public class ToolNode<S extends AgentState> implements Node<S> {

    private final ToolExecutor executor;

    public ToolNode(ToolExecutor executor) {
        this.executor = executor;
    }

    public ToolNode(ToolRegistry registry) {
        this.executor = new ToolExecutor(registry);
    }

    @Override
    public S process(S state) {
        List<Message> messages = state.get("messages");
        if (CollectionUtils.isEmpty(messages)) {
            // 没有消息记录，无法执行工具，静默跳过
            return state;
        }

        Message lastMessage = messages.get(messages.size() - 1);

        // 只有倒数第一条是 AI 发起的调用请求才应当执行处理
        if (!(lastMessage instanceof AiMessage aiMessage)) {
            throw new NodeException("tool_node", "工具节点要求状态中最后一条消息必须是 AiMessage");
        }

        if (!aiMessage.hasToolCalls()) {
            // 没有发生调用求（有些模型可能直接回复普通文本）
            return state;
        }

        // 调用底层独立的执行器完成参数组装与执行
        List<ToolMessage> results = executor.execute(aiMessage);

        // 返回局部更新字典，由于外部使用了 MessagesState，这些新的 ToolMessage 会被安全并且按序追加进整个消息列表
        state.put("messages", results);
        return state;
    }
}
