package org.venus.octopus.api.message;

import java.util.List;
import java.util.Map;

/**
 * AI 模型回复消息
 * <p>支持携带工具调用请求列表（用于 function calling）。</p>
 */
public class AiMessage extends BaseMessage {

    /** 工具调用请求列表，若 AI 不需要调用工具则为空列表 */
    private final List<ToolCall> toolCalls;

    public AiMessage(String content) {
        super(content);
        this.toolCalls = List.of();
    }

    public AiMessage(String content, List<ToolCall> toolCalls) {
        super(content);
        this.toolCalls = toolCalls != null ? List.copyOf(toolCalls) : List.of();
    }

    @Override
    public MessageType getType() {
        return MessageType.AI;
    }

    public List<ToolCall> getToolCalls() {
        return toolCalls;
    }

    /**
     * 判断 AI 是否请求调用工具
     */
    public boolean hasToolCalls() {
        return !toolCalls.isEmpty();
    }

    /**
     * 工具调用请求
     */
    public record ToolCall(String id, String toolName, Map<String, Object> arguments) {}
}
