package org.venus.octopus.api.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息抽象基类
 * <p>
 * 提供消息内容和元数据的默认实现。
 * </p>
 */
public abstract class BaseMessage implements Message {

    private final String content;
    private final Map<String, Object> metadata;

    protected BaseMessage(String content) {
        this.content = content;
        this.metadata = new HashMap<>();
    }

    protected BaseMessage(String content, Map<String, Object> metadata) {
        this.content = content;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    @Override
    public String toString() {
        return getType() + ": " + content;
    }
}
