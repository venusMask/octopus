package org.venus.octopus.core.state;

import org.venus.octopus.api.message.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Dedicated state class supporting a message appending strategy.
 * <p>
 * Implements a natural Reducer principle for the key ("messages"). When putting
 * data into the "messages" key using {@link #put(String, Object)}, if the input
 * is a {@link Message} or Collection&lt;Message&gt;, it will be appended to the
 * end of the existing message queue instead of overwriting the original value.
 * This is crucial for LLM-based continuous conversation agents.
 * </p>
 */
public class MessagesState extends MapAgentState {

    public static final String MESSAGES_KEY = "messages";

    public MessagesState() {
        super();
        // Initialize an empty message queue
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
            // Execute "append" logic based on different input types
            if (value instanceof Collection<?> newMessages) {
                existing.addAll((Collection<? extends Message>) newMessages);
            } else if (value instanceof Message newMessage) {
                existing.add(newMessage);
                // If null is passed and overwrites a message, in principle, it should not be
                // processed or an exception should be thrown; here we choose to safely ignore
                // it.
            } else {
                throw new IllegalArgumentException(
                        "The 'messages' key can only append a Collection of Messages or a single Message object");
            }
        } else {
            // 普通键正常覆盖
            super.put(key, value);
        }
    }

    @Override
    public void merge(Map<String, Object> updates) {
        if (updates != null) {
            // We cannot call super.putAll directly here because we need to reuse the put
            // method overridden in this class to capture the append logic for 'messages'.
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Shortcut to get the message list.
     */
    public List<Message> getMessages() {
        return get(MESSAGES_KEY);
    }
}
