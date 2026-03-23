package org.venus.octopus.core.message;

import org.venus.octopus.api.message.*;
import org.venus.octopus.common.config.Configuration;
import org.venus.octopus.common.config.ReadableConfig;
import org.venus.octopus.common.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 流式消息构建器
 * <p>
 * 提供一套极简流畅的 API 帮助开发者组装向 LLM 发送的消息历史。 支持纯文本，也支持混排 {@link PromptTemplate}。
 * </p>
 */
public class MessageBuilder {

    private final List<Message> messages;
    private ReadableConfig config;

    private MessageBuilder() {
        this.messages = new ArrayList<>();
        this.config = new Configuration();
    }

    /**
     * 创建一个空的消息构建器
     */
    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    /**
     * 配置当前构建器的环境设置
     */
    public MessageBuilder withConfig(ReadableConfig config) {
        if (config != null) {
            this.config = config;
        }
        return this;
    }

    /**
     * 追加已有的消息对象
     */
    public MessageBuilder add(Message message) {
        AssertUtils.notNull(message, "消息不能为 null");
        this.messages.add(message);
        return this;
    }

    /**
     * 追加批量消息
     */
    public MessageBuilder addAll(List<Message> messages) {
        if (messages != null) {
            this.messages.addAll(messages);
        }
        return this;
    }

    /**
     * 追加 System 消息 (纯文本)
     */
    public MessageBuilder system(String content) {
        return add(new SystemMessage(content));
    }

    /**
     * 追加 System 消息 (基于模板对象)
     */
    public MessageBuilder system(PromptTemplate template, Map<String, Object> variables) {
        return system(template.format(variables));
    }

    /**
     * 追加 System 消息 (基于字符串模板和 builder 内部配置)
     */
    public MessageBuilder system(String templateStr, Map<String, Object> variables) {
        PromptTemplate template = new PromptTemplate(templateStr, this.config);
        return system(template.format(variables));
    }

    /**
     * 追加 Human 消息 (纯文本)
     */
    public MessageBuilder human(String content) {
        return add(new HumanMessage(content));
    }

    /**
     * 追加 Human 消息 (基于模板对象)
     */
    public MessageBuilder human(PromptTemplate template, Map<String, Object> variables) {
        return human(template.format(variables));
    }

    /**
     * 追加 Human 消息 (基于字符串模板和 builder 内部配置)
     */
    public MessageBuilder human(String templateStr, Map<String, Object> variables) {
        PromptTemplate template = new PromptTemplate(templateStr, this.config);
        return human(template.format(variables));
    }

    /**
     * 追加 AI 消息 (纯文本)
     */
    public MessageBuilder ai(String content) {
        return add(new AiMessage(content));
    }

    /**
     * 构建最终的消息列表
     *
     * @return 组装好的底层列表副本
     */
    public List<Message> build() {
        return new ArrayList<>(this.messages);
    }
}
