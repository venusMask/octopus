package org.venus.octopus.core.state;

import org.venus.octopus.api.message.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 支持消息追加策略的专用状态类
 * <p>
 * 针对键（"messages"）实现了天然的 Reducer 原理。 当往 "messages" 键
 * {@link #put(String, Object)} 数据时， 如果传入的是一个 {@link Message} 或
 * Collection&lt;Message&gt;，它将被追加到现有消息队列末尾，而不是覆盖原有值。 这对于基于 LLM 的连续对话 Agent
 * 至关重要。
 * </p>
 */
public class MessagesState extends MapAgentState {

    public static final String MESSAGES_KEY = "messages";

    public MessagesState() {
        super();
        // 初始化空的消息队列
        super.put(MESSAGES_KEY, new ArrayList<Message>());
    }

    public MessagesState(Map<String, Object> data) {
        super(data);
        if (!super.containsKey(MESSAGES_KEY)) {
            super.put(MESSAGES_KEY, new ArrayList<Message>());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void put(String key, Object value) {
        if (MESSAGES_KEY.equals(key)) {
            List<Message> existing = get(MESSAGES_KEY);
            if (existing == null) {
                existing = new ArrayList<>();
                super.put(key, existing);
            }
            // 依据不同传入类型执行“追加”逻辑
            if (value instanceof Collection<?> newMessages) {
                existing.addAll((Collection<? extends Message>) newMessages);
            } else if (value instanceof Message newMessage) {
                existing.add(newMessage);
            } else if (value == null) {
                // 如果传入 null 且覆盖的是 message，原则上不作处理或抛异常，这里选择安全地忽略
            } else {
                throw new IllegalArgumentException("messages 键只能追加 Message 集合或单个 Message 对象");
            }
        } else {
            // 普通键正常覆盖
            super.put(key, value);
        }
    }

    @Override
    public void merge(Map<String, Object> updates) {
        if (updates != null) {
            // 这里不能直接调用 super.putAll 因为我们要复用本类覆盖的 put 方法来捕获 messages 的追加逻辑
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 快捷获取消息列表
     */
    public List<Message> getMessages() {
        return get(MESSAGES_KEY);
    }
}
