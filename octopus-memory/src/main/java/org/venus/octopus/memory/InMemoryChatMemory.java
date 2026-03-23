package org.venus.octopus.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.octopus.api.memory.ChatMemory;
import org.venus.octopus.api.message.Message;
import org.venus.octopus.common.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default local in-memory {@link ChatMemory} implementation.
 * <p>
 * Uses thread-safe {@link ConcurrentHashMap} to ensure concurrency correctness.
 * Provides a basic eviction mechanism based on a message count threshold to
 * control memory utilization and avoid LLM token limit issues.
 * </p>
 */
public class InMemoryChatMemory implements ChatMemory {

    private static final Logger log = LoggerFactory.getLogger(InMemoryChatMemory.class);

    private final Map<String, List<Message>> store = new ConcurrentHashMap<>();
    private final int maxMessages;

    /**
     * Default constructor, keeps 100 messages per session by default.
     */
    public InMemoryChatMemory() {
        this(100);
    }

    /**
     * Constructor with custom capacity specification.
     *
     * @param maxMessages
     *            Maximum number of historical messages to keep; when exceeded, the
     *            earliest messages will be removed from the head
     */
    public InMemoryChatMemory(int maxMessages) {
        AssertUtils.isTrue(maxMessages > 0, "maxMessages must be greater than 0");
        this.maxMessages = maxMessages;
        log.info("Initialized InMemoryChatMemory with maxMessages per session = {}", maxMessages);
    }

    @Override
    public void add(String sessionId, List<Message> messages) {
        AssertUtils.notEmpty(sessionId, "SessionId Cannot be empty");
        if (messages == null || messages.isEmpty()) {
            return;
        }

        store.compute(sessionId, (key, existing) -> {
            if (existing == null) {
                existing = new ArrayList<>();
            }
            existing.addAll(messages);

            // Execute window truncation protection mechanism
            if (existing.size() > maxMessages) {
                int oldSize = existing.size();
                existing = new ArrayList<>(existing.subList(existing.size() - maxMessages, existing.size()));
                log.debug(
                        "Session [{}] context has reached the threshold, [{}] earliest records have been safely truncated",
                        sessionId, oldSize - existing.size());
            }
            return existing;
        });
    }

    @Override
    public void add(String sessionId, Message message) {
        if (message != null) {
            add(sessionId, List.of(message));
        }
    }

    @Override
    public List<Message> get(String sessionId) {
        AssertUtils.notEmpty(sessionId, "SessionId Cannot be empty");
        List<Message> history = store.get(sessionId);
        // Returns a safe copy containing the data, ensuring that external modifications
        // do not pollute the internal context
        return history != null ? new ArrayList<>(history) : new ArrayList<>();
    }

    @Override
    public void clear(String sessionId) {
        AssertUtils.notEmpty(sessionId, "SessionId Cannot be empty");
        if (store.remove(sessionId) != null) {
            log.debug("Session [{}] history cleared.", sessionId);
        }
    }
}
