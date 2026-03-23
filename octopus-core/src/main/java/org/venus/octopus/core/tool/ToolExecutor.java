package org.venus.octopus.core.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.octopus.api.message.AiMessage;
import org.venus.octopus.api.message.ToolMessage;
import org.venus.octopus.api.tool.Tool;
import org.venus.octopus.common.exception.ToolException;
import org.venus.octopus.common.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone tool executor.
 * <p>
 * Responsible for parsing {@link AiMessage.ToolCall}, matching the
 * corresponding tool in {@link ToolRegistry}, and executing it. The execution
 * process ensures exception safety. If a tool throws an exception, it will be
 * captured and converted into a {@link ToolMessage} containing the error
 * message to inform the LLM.
 * </p>
 */
public class ToolExecutor {

    private static final Logger log = LoggerFactory.getLogger(ToolExecutor.class);

    private final ToolRegistry registry;

    public ToolExecutor(ToolRegistry registry) {
        AssertUtils.notNull(registry, "ToolRegistry cannot be null");
        this.registry = registry;
    }

    /**
     * Executes the tool call requests contained in the message and generates a set
     * of response results.
     *
     * @param toolCalls
     *            The list of tool call requests sent back by the LLM
     * @return The list of ToolMessage results after execution
     */
    public List<ToolMessage> executeAll(List<AiMessage.ToolCall> toolCalls) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            return List.of();
        }

        List<ToolMessage> results = new ArrayList<>(toolCalls.size());

        for (AiMessage.ToolCall call : toolCalls) {
            String toolName = call.toolName();
            Tool tool = registry.getTool(toolName);

            String resultContent;
            try {
                if (tool == null) {
                    throw new ToolException(toolName,
                            "The requested tool '" + toolName + "' was not found in the registry");
                }

                log.debug("Starting to execute tool [{}] (callId: {}), arguments: {}", toolName, call.id(),
                        call.arguments());
                // 同步执行工具逻辑
                resultContent = tool.execute(call.arguments());
                log.debug("Tool [{}] execution completed, result length: {}", toolName,
                        resultContent != null ? resultContent.length() : 0);

            } catch (Exception e) {
                // Errors during execution need to be captured, and the error message returned
                // to the LLM as a response (this is a standard fault-tolerance practice for
                // Function Calling).
                log.warn("Exception occurred during execution of tool [{}] (callId: {})", toolName, call.id(), e);
                resultContent = "Error executing tool: " + e.getMessage();
            }

            // 特殊情况：如果正常执行但返回 null，转换为明确的说明
            if (resultContent == null) {
                resultContent = "Tool executed successfully but returned no output.";
            }

            results.add(new ToolMessage(call.id(), toolName, resultContent));
        }

        return results;
    }

    /**
     * Extracts tools from AiMessage and executes them. If it's a normal message (no
     * calls), returns an empty list.
     *
     * @param aiMessage
     *            The message sent back by the LLM containing potential call
     *            requests
     * @return Tool execution results
     */
    public List<ToolMessage> execute(AiMessage aiMessage) {
        if (aiMessage == null || !aiMessage.hasToolCalls()) {
            return List.of();
        }
        return executeAll(aiMessage.getToolCalls());
    }
}
