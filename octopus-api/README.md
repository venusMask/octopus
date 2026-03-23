# octopus-api — 接口与SPI定义层

## 模块简介

`octopus-api` 是 Octopus 框架的**接口定义层**，定义了框架中所有核心抽象，供其他模块依赖和实现。该模块仅包含接口、抽象类、枚举和注解，不包含任何具体实现逻辑，旨在保持框架的高内聚低耦合特性。

所有其他模块（octopus-core、octopus-memory、octopus-plugins 等）均以此模块为基础。

---

## 核心功能

### 1. 消息模型（`message` 包）

| 类型 | 说明 |
|---|---|
| `Message` | 消息顶层接口，定义消息的基本信息（类型、内容） |
| `MessageType` | 消息类型枚举：`HUMAN`（用户）/ `AI`（模型）/ `SYSTEM`（系统）/ `TOOL`（工具结果） |
| `BaseMessage` | 消息抽象基类，包含内容和元数据 |
| `HumanMessage` | 用户消息 |
| `AiMessage` | AI 模型回复消息 |
| `SystemMessage` | 系统提示消息 |
| `ToolMessage` | 工具调用结果消息 |

### 2. Agent 状态（`agent` 包）

| 类型 | 说明 |
|---|---|
| `AgentState` | 代理状态接口，本质为类型安全的键值存储，在图节点间传递 |
| `StateReducer<T>` | 状态合并函数式接口，定义同一键的值如何在多次更新时合并（如消息列表追加） |

### 3. 图结构（`graph` 包）

| 类型 | 说明 |
|---|---|
| `Node<S>` | 节点接口，接收状态 S 并返回更新后的状态 |
| `NodeAction<S>` | 函数式节点动作接口（Lambda 友好型） |
| `EdgeCondition<S>` | 边条件判断接口，根据当前状态返回下一个节点名称 |
| `Graph` | 图接口，定义常量（`START`/`END`）和基本元数据 |
| `GraphBuilder<S>` | 图构建器接口，定义 `addNode`/`addEdge`/`addConditionalEdges`/`compile` 等方法 |
| `CompiledGraph<S>` | 已编译图接口，定义 `invoke`（单次运行）和 `stream`（流式运行）方法 |

### 4. 工具（`tool` 包）

| 类型 | 说明 |
|---|---|
| `Tool` | 工具接口，定义工具的执行方法 |
| `ToolSpec` | 工具规格描述，包含名称、描述和参数 Schema，用于生成 LLM 的 function calling 配置 |

---

## 包结构

```
org.venus.octopus.api
├── message/
│   ├── Message.java
│   ├── MessageType.java
│   ├── BaseMessage.java
│   ├── HumanMessage.java
│   ├── AiMessage.java
│   ├── SystemMessage.java
│   └── ToolMessage.java
├── agent/
│   ├── AgentState.java
│   └── StateReducer.java
├── graph/
│   ├── Node.java
│   ├── NodeAction.java
│   ├── EdgeCondition.java
│   ├── Graph.java
│   ├── GraphBuilder.java
│   └── CompiledGraph.java
└── tool/
    ├── Tool.java
    └── ToolSpec.java
```

---

## 依赖关系

```
octopus-api（无框架内依赖，仅依赖 slf4j-api）
    ↑
  被所有其他模块依赖
```

---

## 设计原则

- **面向接口编程**：所有具体实现均在 `octopus-core` 或其他实现模块中
- **最小依赖**：api 模块不依赖 octopus-common，避免循环依赖
- **函数式友好**：`NodeAction`、`EdgeCondition`、`StateReducer` 均为 `@FunctionalInterface`，支持 Lambda 表达式
