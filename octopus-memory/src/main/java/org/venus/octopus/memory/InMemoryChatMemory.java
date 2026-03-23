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
 * 默认的基于本地内存的 {@link ChatMemory} 实现
 * <p>
 * 使用线程安全的 {@link ConcurrentHashMap} 保证并发正确性。
 * 提供基础的按消息条数阈值淘汰机制以控制内存利用率和避免大模型超限问题。
 * </p>
 */
public class InMemoryChatMemory implements ChatMemory {

    private static final Logger log = LoggerFactory.getLogger(InMemoryChatMemory.class);

    private final Map<String, List<Message>> store = new ConcurrentHashMap<>();
    private final int maxMessages;

    /**
     * 默认构造函数，单个会话历史默认保留 100 条
     */
    public InMemoryChatMemory() {
        this(100);
    }

    /**
     * 自定义容量规格的构造函数
     *
     * @param maxMessages
     *            保留的最大历史消息数，超过时将从头部剔除最早的消息
     */
    public InMemoryChatMemory(int maxMessages) {
        AssertUtils.isTrue(maxMessages > 0, "maxMessages 必须大于 0");
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

            // 执行窗口截断保护机制
            if (existing.size() > maxMessages) {
                int oldSize = existing.size();
                existing = new ArrayList<>(existing.subList(existing.size() - maxMessages, existing.size()));
                log.debug("Session [{}] 上下文已达阈值, [{}] 条最早记录已被安全截断", sessionId, oldSize - existing.size());
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
        // 返回包含数据的安全副本，保证外部修改不污染内部上下文
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
