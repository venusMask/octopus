package org.venus.octopus.core.message;

import org.venus.octopus.api.message.*;
import org.venus.octopus.common.config.Configuration;
import org.venus.octopus.common.config.ReadableConfig;
import org.venus.octopus.common.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fluent message builder.
 * <p>
 * Provides a minimalist and fluent API to help developers assemble message
 * history for LLM. Supports plain text as well as mixed {@link PromptTemplate}.
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
     * Creates an empty message builder.
     */
    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    /**
     * Configures the environment settings for the current builder.
     */
    public MessageBuilder withConfig(ReadableConfig config) {
        if (config != null) {
            this.config = config;
        }
        return this;
    }

    /**
     * Appends an existing message object.
     */
    public MessageBuilder add(Message message) {
        AssertUtils.notNull(message, "Message cannot be null");
        this.messages.add(message);
        return this;
    }

    /**
     * Appends messages in batch.
     */
    public MessageBuilder addAll(List<Message> messages) {
        if (messages != null) {
            this.messages.addAll(messages);
        }
        return this;
    }

    /**
     * Appends a System message (plain text).
     */
    public MessageBuilder system(String content) {
        return add(new SystemMessage(content));
    }

    /**
     * Appends a System message (based on a template object).
     */
    public MessageBuilder system(PromptTemplate template, Map<String, Object> variables) {
        return system(template.format(variables));
    }

    /**
     * Appends a System message (based on a string template and builder internal
     * configuration).
     */
    public MessageBuilder system(String templateStr, Map<String, Object> variables) {
        PromptTemplate template = new PromptTemplate(templateStr, this.config);
        return system(template.format(variables));
    }

    /**
     * Appends a Human message (plain text).
     */
    public MessageBuilder human(String content) {
        return add(new HumanMessage(content));
    }

    /**
     * Appends a Human message (based on a template object).
     */
    public MessageBuilder human(PromptTemplate template, Map<String, Object> variables) {
        return human(template.format(variables));
    }

    /**
     * Appends a Human message (based on a string template and builder internal
     * configuration).
     */
    public MessageBuilder human(String templateStr, Map<String, Object> variables) {
        PromptTemplate template = new PromptTemplate(templateStr, this.config);
        return human(template.format(variables));
    }

    /**
     * Appends an AI message (plain text).
     */
    public MessageBuilder ai(String content) {
        return add(new AiMessage(content));
    }

    /**
     * Builds the final message list.
     *
     * @return A copy of the assembled underlying list
     */
    public List<Message> build() {
        return new ArrayList<>(this.messages);
    }
}
