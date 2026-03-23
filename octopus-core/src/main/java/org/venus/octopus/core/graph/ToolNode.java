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
 * Built-in tool node that encapsulates fully autonomous processing logic.
 * <p>
 * It assumes that AgentState contains a key named "messages". At runtime, it
 * reads the content from the last message. If it is determined to be an
 * AiMessage requiring tool calls, it is forwarded to the {@link ToolExecutor}.
 * The final list of {@link ToolMessage} will be returned sealed in a brand new
 * Map.
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
            // No message history, unable to execute tools, silently skipping
            return state;
        }

        Message lastMessage = messages.get(messages.size() - 1);

        // Processing should only be executed if the last message is a call request
        // initiated by AI
        if (!(lastMessage instanceof AiMessage aiMessage)) {
            throw new NodeException("tool_node",
                    "The tool node requires that the last message in the state must be an AiMessage");
        }

        if (!aiMessage.hasToolCalls()) {
            // No call request occurred (some models may reply directly with plain text)
            return state;
        }

        // Invoke the underlying standalone executor to complete parameter assembly and
        // execution
        List<ToolMessage> results = executor.execute(aiMessage);

        // Returns a local update dictionary. Since a MessagesState is used externally,
        // these new ToolMessages will be safely appended to the entire message list in
        // order.
        state.put("messages", results);
        return state;
    }
}
