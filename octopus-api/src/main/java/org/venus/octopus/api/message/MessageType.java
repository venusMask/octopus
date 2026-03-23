package org.venus.octopus.api.message;

/**
 * 消息类型枚举
 */
public enum MessageType {
    /** 用户消息 */
    HUMAN,
    /** AI 模型回复消息 */
    AI,
    /** 系统提示消息 */
    SYSTEM,
    /** 工具调用结果消息 */
    TOOL
}
