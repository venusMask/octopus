package org.venus.octopus.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.venus.octopus.api.message.HumanMessage;
import org.venus.octopus.api.message.Message;
import org.venus.octopus.api.message.SystemMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryChatMemoryTest {

    private InMemoryChatMemory memory;

    @BeforeEach
    void setUp() {
        // Set a small capacity to facilitate testing the truncation mechanism
        memory = new InMemoryChatMemory(3);
    }

    @Test
    void testBasicAddAndGet() {
        String session1 = "user-123";
        String session2 = "user-456";

        // Write data separately
        memory.add(session1, new SystemMessage("You are an assistant."));
        memory.add(session1, new HumanMessage("Hello"));

        memory.add(session2, new HumanMessage("Hi there"));

        // Verify isolation and correctness
        List<Message> s1History = memory.get(session1);
        assertEquals(2, s1History.size());
        assertEquals("You are an assistant.", s1History.get(0).getContent());
        assertEquals("Hello", s1History.get(1).getContent());

        List<Message> s2History = memory.get(session2);
        assertEquals(1, s2History.size());
        assertEquals("Hi there", s2History.get(0).getContent());
    }

    @Test
    void testCapacityTruncation() {
        String session = "session-truncation";

        // The capacity limit is 3, we insert 5 messages
        memory.add(session, new HumanMessage("Message 1"));
        memory.add(session, new HumanMessage("Message 2"));
        memory.add(session, new HumanMessage("Message 3"));
        memory.add(session, new HumanMessage("Message 4"));
        memory.add(session, new HumanMessage("Message 5"));

        List<Message> history = memory.get(session);
        // Only the 3 most recently submitted messages should remain
        assertEquals(3, history.size());
        assertEquals("Message 3", history.get(0).getContent());
        assertEquals("Message 4", history.get(1).getContent());
        assertEquals("Message 5", history.get(2).getContent());
    }

    @Test
    void testClear() {
        String session = "session-clear";
        memory.add(session, new HumanMessage("Test record"));
        assertFalse(memory.get(session).isEmpty());

        memory.clear(session);
        assertTrue(memory.get(session).isEmpty());
    }
}
