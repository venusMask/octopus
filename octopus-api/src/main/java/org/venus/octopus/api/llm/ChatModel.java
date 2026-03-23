package org.venus.octopus.api.llm;

import org.venus.octopus.api.message.Message;

import java.util.List;

/**
 * 统一的大模型客户端接口
 * <p>
 * 框架的基座标准，所有对接外部不同厂商（如 OpenAI、DeepSeek、Moonshot 等）的插件工程均必须实现该接口。
 * 解耦内部核心逻辑与网络协议调用的强绑定。
 * </p>
 */
public interface ChatModel extends Model<ChatRequest, ChatResponse> {

    @Override
    ChatResponse call(ChatRequest request);

    default ChatResponse call(List<Message> messages) {
        return call(ChatRequest.builder().messages(messages).build());
    }

}
