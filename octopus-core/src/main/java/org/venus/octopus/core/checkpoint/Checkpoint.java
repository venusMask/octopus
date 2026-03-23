package org.venus.octopus.core.checkpoint;

import org.venus.octopus.api.agent.AgentState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 检查点接口
 * <p>
 * 检查点在每个节点执行完毕后保存状态快照，支持从历史检查点恢复执行（断点续跑）。
 * 不同的 threadId 对应不同用户/会话的执行上下文，互相隔离。
 * </p>
 *
 * @param <S> AgentState 类型
 */
public interface Checkpoint<S extends AgentState> {

    /**
     * 保存一个检查点
     *
     * @param threadId 线程/会话 ID
     * @param nodeName 刚执行完毕的节点名称
     * @param state    节点执行后的状态
     */
    void save(String threadId, String nodeName, S state);

    /**
     * 获取最新的检查点状态
     *
     * @param threadId 线程/会话 ID
     * @return 最新状态，若无则返回 empty
     */
    Optional<S> getLatest(String threadId);

    /**
     * 获取指定线程的所有检查点列表（按保存顺序）
     *
     * @param threadId 线程/会话 ID
     * @return 检查点快照列表
     */
    List<CheckpointEntry<S>> getHistory(String threadId);

    /**
     * 清除指定线程的所有检查点
     *
     * @param threadId 线程/会话 ID
     */
    void clear(String threadId);

    /**
     * 单次检查点记录
     *
     * @param nodeName  节点名称
     * @param state     状态快照
     * @param timestamp 保存时间戳（毫秒）
     */
    record CheckpointEntry<S extends AgentState>(String nodeName, S state, long timestamp) {}
}
