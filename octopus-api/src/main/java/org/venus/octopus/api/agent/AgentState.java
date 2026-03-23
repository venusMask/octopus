package org.venus.octopus.api.agent;

import java.util.Map;
import java.util.Optional;

/**
 * Agent 状态接口
 * <p>
 * AgentState 是图流程编排中节点间传递数据的核心载体，本质上是一个类型安全的键值存储。
 * 每个节点接收当前状态，执行完毕后返回更新的状态片段，由框架自动合并到全局状态。
 * </p>
 *
 * <p>
 * 使用示例：
 * </p>
 * 
 * <pre>{@code
 * AgentState state = new MapAgentState();
 * state.put("messages", List.of(new HumanMessage("你好")));
 * state.put("next", "agent");
 *
 * String next = state.get("next"); // "agent"
 * }</pre>
 */
public interface AgentState {

    /**
     * 根据键获取值
     *
     * @param key
     *            键
     * @return 对应的值，若不存在则返回 null
     */
    <T> T get(String key);

    /**
     * 根据键安全地获取值，返回 Optional
     *
     * @param key
     *            键
     * @return Optional 包装的值
     */
    <T> Optional<T> getOptional(String key);

    /**
     * 设置键值对
     *
     * @param key
     *            键
     * @param value
     *            值
     */
    void put(String key, Object value);

    /**
     * 批量合并键值对（将 updates 中的所有条目合并到当前状态）
     *
     * @param updates
     *            待合并的状态更新
     */
    void merge(Map<String, Object> updates);

    /**
     * 将当前状态转为不可变 Map 视图
     *
     * @return 状态的 Map 表示
     */
    Map<String, Object> toMap();

    /**
     * 判断是否包含指定键
     *
     * @param key
     *            键
     * @return 是否包含
     */
    boolean containsKey(String key);

    /**
     * 创建当前状态的浅拷贝
     *
     * @return 状态副本
     */
    AgentState copy();
}
