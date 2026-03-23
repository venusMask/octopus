package org.venus.octopus.core.graph;

import org.junit.jupiter.api.Test;
import org.venus.octopus.api.message.*;
import org.venus.octopus.api.tool.Tool;
import org.venus.octopus.api.tool.ToolSpec;
import org.venus.octopus.core.state.MessagesState;
import org.venus.octopus.core.tool.ToolRegistry;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ToolNodeTest {

    @Test
    void testMessagesStateAppendLogic() {
        MessagesState state = new MessagesState();

        // 测试追加单个 Message
        state.put(MessagesState.MESSAGES_KEY, new SystemMessage("You are a helpful assistant."));
        assertEquals(1, state.getMessages().size());
        assertEquals("You are a helpful assistant.", state.getMessages().get(0).getContent());

        // 测试追加多个 Message (如 ToolNode 返回的多条结果)
        state.put(MessagesState.MESSAGES_KEY,
                List.of(new HumanMessage("What is the weather?"), new AiMessage("Wait a second...",
                        List.of(new AiMessage.ToolCall("call_1", "getWeather", Map.of("city", "Beijing"))))));

        assertEquals(3, state.getMessages().size());
        assertInstanceOf(SystemMessage.class, state.getMessages().get(0));
        assertInstanceOf(HumanMessage.class, state.getMessages().get(1));
        assertInstanceOf(AiMessage.class, state.getMessages().get(2));
    }

    @Test
    void testToolNodeExecution() {
        // 1. 准备工具注册表与虚拟工具
        ToolRegistry registry = new ToolRegistry();
        registry.register(new Tool() {
            @Override
            public String getName() {
                return "getWeather";
            }

            @Override
            public ToolSpec getSpec() {
                return ToolSpec.builder().name("getWeather").description("Get weather").build();
            }

            @Override
            public String execute(Map<String, Object> arguments) {
                String city = (String) arguments.get("city");
                if ("Beijing".equals(city)) {
                    return "Sunny, 25°C";
                }
                return "Unknown";
            }
        });

        // 2. 初始化图状态，模拟大模型刚刚返回了 ToolCall
        MessagesState state = new MessagesState();
        state.put(MessagesState.MESSAGES_KEY, new SystemMessage("Sys"));
        AiMessage aiMessage = new AiMessage("Let me check",
                List.of(new AiMessage.ToolCall("call_weather_1", "getWeather", Map.of("city", "Beijing")),
                        new AiMessage.ToolCall("call_weather_2", "getWeather", Map.of("city", "Shanghai"))));
        state.put(MessagesState.MESSAGES_KEY, aiMessage);

        // 3. 构造并调用 ToolNode
        ToolNode<MessagesState> toolNode = new ToolNode<>(registry);

        MessagesState updatedState = toolNode.process(state);

        // 4. 验证工具执行结果（返回的应该是已合并过工具回包的的状态）
        assertNotNull(updatedState);

        List<Message> newMessages = updatedState.getMessages();

        // 5. 验证将局部更新 merge 回原状态后，消息总量应该是 1 (System) + 1 (AI) + 2 (Tools) = 4
        assertEquals(4, newMessages.size(), "由ToolNode追加之后的历史总量应为4条");

        ToolMessage msg1 = (ToolMessage) newMessages.get(2);
        assertEquals("call_weather_1", msg1.getToolCallId());
        assertEquals("getWeather", msg1.getToolName());
        assertEquals("Sunny, 25°C", msg1.getContent());

        ToolMessage msg2 = (ToolMessage) newMessages.get(3);
        assertEquals("call_weather_2", msg2.getToolCallId());
        assertEquals("getWeather", msg2.getToolName());
        assertEquals("Unknown", msg2.getContent());
        assertInstanceOf(ToolMessage.class, newMessages.get(3));
    }
}
