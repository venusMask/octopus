package org.venus.octopus.core.checkpoint;

import org.venus.octopus.api.agent.AgentState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的检查点实现
 * <p>
 * 将所有检查点保存在内存中，适用于开发测试场景。
 * 进程重启后数据丢失，如需持久化，可实现 {@link Checkpoint} 接口并接入数据库。
 * </p>
 * <p>本实现是线程安全的。</p>
 *
 * @param <S> AgentState 类型
 */
public class InMemoryCheckpoint<S extends AgentState> implements Checkpoint<S> {

    /** key: threadId, value: 该线程的检查点历史列表 */
    private final Map<String, List<CheckpointEntry<S>>> store = new ConcurrentHashMap<>();

    @Override
    public void save(String threadId, String nodeName, S state) {
        CheckpointEntry<S> entry = new CheckpointEntry<>(nodeName, state, System.currentTimeMillis());
        store.computeIfAbsent(threadId, k -> new ArrayList<>()).add(entry);
    }

    @Override
    public Optional<S> getLatest(String threadId) {
        List<CheckpointEntry<S>> entries = store.get(threadId);
        if (entries == null || entries.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(entries.get(entries.size() - 1).state());
    }

    @Override
    public List<CheckpointEntry<S>> getHistory(String threadId) {
        List<CheckpointEntry<S>> entries = store.get(threadId);
        if (entries == null) {
            return List.of();
        }
        return Collections.unmodifiableList(entries);
    }

    @Override
    public void clear(String threadId) {
        store.remove(threadId);
    }

    /**
     * 获取所有 threadId 列表
     */
    public Set<String> getAllThreadIds() {
        return Collections.unmodifiableSet(store.keySet());
    }
}
