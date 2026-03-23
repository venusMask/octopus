package org.venus.octopus.api.llm;

import org.venus.octopus.api.message.Message;
import org.venus.octopus.api.tool.ToolSpec;
import org.venus.octopus.common.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装并规范单次对话模型被唤起时所需传递的全部复杂关联上下文。
 */
public class ChatRequest {

    private final List<Message> messages;
    private final ChatOptions options;
    private final List<ToolSpec> tools;

    private ChatRequest(Builder builder) {
        AssertUtils.notNull(builder.messages, "Messages Cannot be null");
        this.messages = new ArrayList<>(builder.messages);
        this.options = builder.options;
        this.tools = builder.tools != null ? new ArrayList<>(builder.tools) : null;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public ChatOptions getOptions() {
        return options;
    }

    public List<ToolSpec> getTools() {
        return tools;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Message> messages;
        private ChatOptions options;
        private List<ToolSpec> tools;

        public Builder messages(List<Message> messages) {
            this.messages = messages;
            return this;
        }

        public Builder options(ChatOptions options) {
            this.options = options;
            return this;
        }

        public Builder tools(List<ToolSpec> tools) {
            this.tools = tools;
            return this;
        }

        public ChatRequest build() {
            return new ChatRequest(this);
        }
    }
}
