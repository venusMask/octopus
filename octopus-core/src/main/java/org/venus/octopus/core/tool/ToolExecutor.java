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
 * 独立的工具执行器
 * <p>
 * 专门负责解析 {@link AiMessage.ToolCall}，在 {@link ToolRegistry} 中匹配相应的工具并执行。
 * 执行过程保证异常安全，若工具抛出异常，将捕获并转化为内容为错误信息的 {@link ToolMessage} 告知 LLM。
 * </p>
 */
public class ToolExecutor {

    private static final Logger log = LoggerFactory.getLogger(ToolExecutor.class);

    private final ToolRegistry registry;

    public ToolExecutor(ToolRegistry registry) {
        AssertUtils.notNull(registry, "ToolRegistry 不能为 null");
        this.registry = registry;
    }

    /**
     * 执行消息中包含的发起调用请求，生成响应结果集合
     *
     * @param toolCalls
     *            LLM发回的工具调用请求列表
     * @return 执行完成后的 ToolMessage 结果列表
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
                    throw new ToolException(toolName, "请求调用的工具 '" + toolName + "' 未在注册表中找到");
                }

                log.debug("开始执行工具 [{}] (callId: {})，参数: {}", toolName, call.id(), call.arguments());
                // 同步执行工具逻辑
                resultContent = tool.execute(call.arguments());
                log.debug("工具 [{}] 执行完毕，结果长度: {}", toolName, resultContent != null ? resultContent.length() : 0);

            } catch (Exception e) {
                // 执行过程出错需要捕获，将错误信息作为回复返回给 LLM（这是标准 Function Calling 的容错做法）
                log.warn("工具 [{}] (callId: {}) 执行发生异常", toolName, call.id(), e);
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
     * 从 AiMessage 中提取工具并执行。如果是普通消息（无调用），则返回空列表。
     *
     * @param aiMessage
     *            LLM 发回的包含潜在调用请求的消息
     * @return 工具执行结果
     */
    public List<ToolMessage> execute(AiMessage aiMessage) {
        if (aiMessage == null || !aiMessage.hasToolCalls()) {
            return List.of();
        }
        return executeAll(aiMessage.getToolCalls());
    }
}
