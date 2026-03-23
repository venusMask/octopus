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
        // 为了方便测试截断机制，这里设置一个小容量
        memory = new InMemoryChatMemory(3);
    }

    @Test
    void testBasicAddAndGet() {
        String session1 = "user-123";
        String session2 = "user-456";

        // 分别写入数据
        memory.add(session1, new SystemMessage("You are an assistant."));
        memory.add(session1, new HumanMessage("Hello"));

        memory.add(session2, new HumanMessage("Hi there"));

        // 验证隔离和正确性
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

        // 容量上限是 3，我们插入 5 条数据
        memory.add(session, new HumanMessage("Message 1"));
        memory.add(session, new HumanMessage("Message 2"));
        memory.add(session, new HumanMessage("Message 3"));
        memory.add(session, new HumanMessage("Message 4"));
        memory.add(session, new HumanMessage("Message 5"));

        List<Message> history = memory.get(session);
        // 应该只剩下最近被提交的 3 条
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
