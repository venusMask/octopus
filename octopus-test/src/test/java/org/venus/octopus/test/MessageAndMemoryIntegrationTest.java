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
        // 1. Initialize MessagesState containing the special Reducer append mechanism
        MessagesState state = new MessagesState();

        // 2. Simulate message pipeline assembly for the LLM workbench
        Message firstHuman = MessageBuilder.builder().human("What's the weather like in New York?").build().get(0);

        // State receives the first new element and appends it automatically
        state.put("messages", List.of(firstHuman));

        // Simulate LLM wanting to call the weather interface
        AiMessage toolCallMsg = new AiMessage("",
                List.of(new AiMessage.ToolCall("call_123", "get_weather", Map.of("location", "New York"))));
        state.put("messages", List.of(toolCallMsg));

        // Simulate backfilling after external tool execution
        ToolMessage toolMsg = new ToolMessage("call_123", "get_weather", "Sunny, 25°C");
        state.put("messages", List.of(toolMsg));

        // Assertion: although the same key is used, the collection automatically stacks
        // to 3 items
        @SuppressWarnings("unchecked")
        List<Message> finalMessages = (List<Message>) state.get("messages");
        assertEquals(3, finalMessages.size());
        assertTrue(finalMessages.get(0) instanceof HumanMessage);
        assertTrue(finalMessages.get(1) instanceof AiMessage);
        assertTrue(finalMessages.get(2) instanceof ToolMessage);

        // 3. Test the sliding window persistence and truncation of Memory
        // Sliding window set to 2 (keeps only the two most recent interaction records
        // to prevent excessively large history)
        ChatMemory memory = new InMemoryChatMemory(2);
        memory.add("session-1", finalMessages);

        List<Message> retrieved = memory.get("session-1");

        // Since maxMessages=2 is set, the first sentence (HumanMessage) should be
        // evicted; here it is asserted as 2
        assertEquals(2, retrieved.size());
        assertTrue(retrieved.get(0) instanceof AiMessage);
        assertTrue(retrieved.get(1) instanceof ToolMessage);
    }
}
