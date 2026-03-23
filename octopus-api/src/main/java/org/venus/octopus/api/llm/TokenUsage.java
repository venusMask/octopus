package org.venus.octopus.api.llm;

/**
 * 记录单次大模型调用的 Token 消耗信息
 */
public record TokenUsage(int promptTokens, int completionTokens, int totalTokens) {

    /**
     * 空消耗常量，用于模拟或流式初期无法获取 Token 时的默认返回
     */
    public static final TokenUsage EMPTY = new TokenUsage(0, 0, 0);
}
