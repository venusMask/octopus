package org.venus.octopus.api.message;

/**
 * 工具调用结果消息
 */
public class ToolMessage extends BaseMessage {

    /** 对应的工具调用 ID */
    private final String toolCallId;
    /** 工具名称 */
    private final String toolName;

    public ToolMessage(String toolCallId, String toolName, String content) {
        super(content);
        this.toolCallId = toolCallId;
        this.toolName = toolName;
    }

    @Override
    public MessageType getType() {
        return MessageType.TOOL;
    }

    public String getToolCallId() {
        return toolCallId;
    }

    public String getToolName() {
        return toolName;
    }
}
