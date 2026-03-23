# octopus-memory — 记忆与上下文管理

## 模块简介

`octopus-memory` 提供 Agent 的**记忆管理**能力，支持短期记忆（对话历史）和长期记忆（持久化知识）的统一抽象。记忆模块与 `octopus-core` 解耦，可独立配置和替换。

---

## 核心功能

### 1. 短期记忆（会话记忆）

管理单次对话中的消息历史，支持：
- **滑动窗口截断**：当消息数量超过阈值时，保留最新的 N 条消息，防止 Token 溢出
- **Token 限制截断**：基于 Token 估算限制历史长度
- **多会话隔离**：按 `sessionId` 区分不同用户的对话上下文

**核心接口**：
```java
public interface ShortTermMemory {
    void addMessage(String sessionId, Message message);
    List<Message> getHistory(String sessionId);
    void clearSession(String sessionId);
    List<Message> getTruncatedHistory(String sessionId, int maxMessages);
}
```

### 2. 长期记忆（持久化记忆）

支持跨会话的知识持久化：
- **向量记忆**：基于语义相似度检索（接入向量数据库）
- **摘要记忆**：将历史对话压缩为摘要后存储
- **实体记忆**：提取对话中的关键实体（用户偏好、重要事实等）并持久化

**核心接口**：
```java
public interface LongTermMemory {
    void save(String userId, String key, Object value);
    Object recall(String userId, String key);
    List<String> search(String userId, String query, int topK);
}
```

### 3. 记忆管理器

`MemoryManager` 统一管理短期和长期记忆，在 `StateGraph` 执行时自动注入：

```java
MemoryManager memory = MemoryManager.builder()
    .shortTerm(new InMemoryShortTermMemory(maxMessages = 20))
    .longTerm(new InMemoryLongTermMemory())
    .build();
```

---

## 包结构

```
org.venus.octopus.memory
├── ShortTermMemory.java            # 短期记忆接口
├── LongTermMemory.java             # 长期记忆接口
├── MemoryManager.java              # 记忆管理器
├── impl/
│   ├── InMemoryShortTermMemory.java   # 内存短期记忆实现
│   └── InMemoryLongTermMemory.java    # 内存长期记忆实现
└── strategy/
    ├── TruncationStrategy.java        # 截断策略接口
    ├── SlidingWindowStrategy.java     # 滑动窗口截断
    └── TokenLimitStrategy.java        # Token 限制截断
```

---

## 依赖关系

```
octopus-api
    ↑
octopus-memory（本模块，可被 octopus-core 或用户代码引用）
```
