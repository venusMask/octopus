package org.venus.octopus.api.message;

import java.util.Map;

/**
 * 消息顶层接口
 * <p>
 * 代表 Agent 与用户、工具或 AI 模型之间交换的单条消息。
 * </p>
 */
public interface Message {

    /**
     * 获取消息类型
     */
    MessageType getType();

    /**
     * 获取消息内容
     */
    String getContent();

    /**
     * 获取消息元数据（可为空 Map）
     */
    Map<String, Object> getMetadata();
}
