package org.venus.octopus.api.llm;

import org.venus.octopus.api.message.Message;
import org.venus.octopus.api.tool.ToolSpec;

import java.util.List;

/**
 * 统一的大模型客户端接口
 * <p>
 * 框架的基座标准，所有对接外部不同厂商（如 OpenAI、DeepSeek、Moonshot 等）的插件工程均必须实现该接口。
 * 解耦内部核心逻辑与网络协议调用的强绑定。
 * </p>
 */
public interface ChatModel {

    /**
     * 发起基础的多轮对话调用
     *
     * @param messages
     *            整个会话的消息历史上下文
     * @return 封装包含最新 AiMessage 文本的生成结果
     */
    default ChatResponse generate(List<Message> messages) {
        return generate(messages, null, null);
    }

    /**
     * 发起带参数和工具定义的高阶调用
     *
     * @param messages
     *            历史消息
     * @param options
     *            超参数配置（可为空，表示使用厂商默认参数）
     * @param tools
     *            模型被允许选用的工具定义（可为空）
     * @return 包含文本内容或潜在工具调用指令的 {@link ChatResponse}
     */
    ChatResponse generate(List<Message> messages, ChatOptions options, List<ToolSpec> tools);

}
