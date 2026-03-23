package org.venus.octopus.test;

import org.junit.jupiter.api.Test;
import org.venus.octopus.api.memory.ChatMemory;
import org.venus.octopus.api.message.AiMessage;
import org.venus.octopus.api.message.HumanMessage;
import org.venus.octopus.api.message.Message;
import org.venus.octopus.api.message.ToolMessage;
import org.venus.octopus.core.message.MessageBuilder;
import org.venus.octopus.core.state.MessagesState;
import org.venus.octopus.memory.InMemoryChatMemory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageAndMemoryIntegrationTest {

    @Test
    void testMessageAppendingAndMemoryWindowTruncation() {
        // 1. 初始化包含特殊 Reducer 追加机制的 MessagesState
        MessagesState state = new MessagesState();

        // 2. 模拟 LLM 工作台的消息流水线组装
        Message firstHuman = MessageBuilder.builder().human("What's the weather like in New York?").build().get(0);

        // State 接收第一次新要素，自动追加
        state.put("messages", List.of(firstHuman));

        // 模拟 LLM 想调用天气接口
        AiMessage toolCallMsg = new AiMessage("",
                List.of(new AiMessage.ToolCall("call_123", "get_weather", Map.of("location", "New York"))));
        state.put("messages", List.of(toolCallMsg));

        // 模拟外部工具执行后的回填
        ToolMessage toolMsg = new ToolMessage("call_123", "get_weather", "Sunny, 25°C");
        state.put("messages", List.of(toolMsg));

        // 断言：虽然用了相同的 key，但集合自动叠加到了 3 条
        @SuppressWarnings("unchecked")
        List<Message> finalMessages = (List<Message>) state.get("messages");
        assertEquals(3, finalMessages.size());
        assertTrue(finalMessages.get(0) instanceof HumanMessage);
        assertTrue(finalMessages.get(1) instanceof AiMessage);
        assertTrue(finalMessages.get(2) instanceof ToolMessage);

        // 3. 测试 Memory 的滑动窗口持久化与截断
        // 滑动窗口设为 2（只保留最新两条交互记录，防止历史过大）
        ChatMemory memory = new InMemoryChatMemory(2);
        memory.add("session-1", finalMessages);

        List<Message> retrieved = memory.get("session-1");

        // 由于设置了 maxMessages=2，第一句话 (HumanMessage) 应该被剔除，这里断言为 2
        assertEquals(2, retrieved.size());
        assertTrue(retrieved.get(0) instanceof AiMessage);
        assertTrue(retrieved.get(1) instanceof ToolMessage);
    }
}
