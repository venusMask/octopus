package org.venus.octopus.api.memory;

import org.venus.octopus.api.message.Message;

import java.util.List;

/**
 * 会话上下文记忆接口
 * <p>
 * 提供长期或短期的多轮对话上下文管理能力，以 sessionId 作为区分用户的标识。 可用于普通的 LLM 连续对话，也可作为 Agent
 * 执行过程中的重要知识补充层。
 * </p>
 */
public interface ChatMemory {

    /**
     * 追加多条消息到指定会话
     *
     * @param sessionId
     *            会话的唯一标识
     * @param messages
     *            需要追缴的消息列表
     */
    void add(String sessionId, List<Message> messages);

    /**
     * 追加单条消息到指定会话
     *
     * @param sessionId
     *            会话的唯一标识
     * @param message
     *            单条新增消息
     */
    void add(String sessionId, Message message);

    /**
     * 获取指定会话的所有历史消息（受特定实现类内部的截断淘汰策略控制）
     *
     * @param sessionId
     *            会话标识
     * @return 历史消息的不可变列表或安全副本，如果从不存在则返回空列表
     */
    List<Message> get(String sessionId);

    /**
     * 清除指定会话的所有内容
     *
     * @param sessionId
     *            会话标识
     */
    void clear(String sessionId);
}
