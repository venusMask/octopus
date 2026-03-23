package org.venus.octopus.api.llm;

import org.venus.octopus.api.message.AiMessage;
import org.venus.octopus.common.utils.AssertUtils;

/**
 * 统一的大模型生成结果封装
 * <p>
 * 包含模型生成的实际 {@link AiMessage} 内容，以及此次调用的元数据（如 Token 消耗等）。
 * </p>
 */
public class ChatResponse {

    private final AiMessage aiMessage;
    private final TokenUsage tokenUsage;

    public ChatResponse(AiMessage aiMessage, TokenUsage tokenUsage) {
        AssertUtils.notNull(aiMessage, "AiMessage Cannot be null");
        this.aiMessage = aiMessage;
        this.tokenUsage = tokenUsage != null ? tokenUsage : TokenUsage.EMPTY;
    }

    public AiMessage getMessage() {
        return aiMessage;
    }

    public TokenUsage getTokenUsage() {
        return tokenUsage;
    }

    @Override
    public String toString() {
        return "ChatResponse{" + "aiMessage=" + aiMessage + ", tokenUsage=" + tokenUsage + '}';
    }
}
