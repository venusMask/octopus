# octopus-core — 核心引擎实现

## 模块简介

`octopus-core` 是 Octopus 框架的**核心实现模块**，提供图流程编排引擎的完整实现。它实现了 `octopus-api` 中定义的所有核心接口，是整个框架的运行基础。

类比 LangGraph（Python），`octopus-core` 提供与之对等的能力：定义节点、构建图、编译图、运行图、管理状态。

---

## 核心功能

### 1. 状态管理（`state` 包）

| 类型 | 说明 |
|---|---|
| `MapAgentState` | 基于 `HashMap` 的 `AgentState` 实现，支持任意类型值的存储与获取 |

**状态设计理念**：
- AgentState 在每个节点执行时作为**输入和输出**
- 节点返回新的状态，框架自动将其**合并**（Merge）到全局状态中
- 通过 `StateReducer` 支持自定义合并逻辑（如消息列表追加而非覆盖）

### 2. 图构建（`graph` 包）

| 类型 | 说明 |
|---|---|
| `StateGraph<S>` | 核心构建器，类似 LangGraph 的 `StateGraph`。提供 `addNode`、`addEdge`、`addConditionalEdges`、`compile` 等 API |
| `GraphNode<S>` | 节点的内部包装类，存储节点名称和执行逻辑 |
| `GraphEdge` | 边的内部描述类，区分直接边（Direct）和条件边（Conditional） |
| `GraphDefinition<S>` | 图的结构定义（节点表 + 边表），是 `StateGraph` 构建的中间产物 |
| `CompiledGraphImpl<S>` | 实现 `CompiledGraph` 接口，持有图定义和运行器，提供 `invoke`/`stream` 方法 |

**核心 API 示例**：
```java
StateGraph<MapAgentState> graph = new StateGraph<>(MapAgentState::new)
    .addNode("agent", state -> agentNode(state))
    .addNode("tools", state -> toolsNode(state))
    .addEdge(Graph.START, "agent")
    .addConditionalEdges("agent",
        state -> shouldContinue(state) ? "tools" : "end",
        Map.of("tools", "tools", "end", Graph.END))
    .addEdge("tools", "agent");

CompiledGraph<MapAgentState> app = graph.compile();
MapAgentState result = app.invoke(initialState);
```

### 3. 图运行引擎（`runner` 包）

| 类型 | 说明 |
|---|---|
| `GraphRunner<S>` | 图执行引擎，实现**状态机循环**：从 `START` 节点出发，根据边的条件跳转，直到到达 `END` 节点 |

**执行流程**：
```
START
  │
  ▼
 [node_1] ──直接边──→ [node_2] ──条件边──→ [node_3 or END]
                                  ↑
                              (路由函数决定)
```

运行引擎支持：
- **最大迭代次数**限制（防止无限循环）
- **节点异常捕获与传播**
- **流式执行**（逐节点回调）

### 4. 检查点（`checkpoint` 包）

| 类型 | 说明 |
|---|---|
| `Checkpoint<S>` | 检查点接口，定义状态快照的保存和恢复 |
| `InMemoryCheckpoint<S>` | 基于内存的检查点实现，用于开发测试；如需持久化，可接入数据库实现 |

检查点支持：
- 每个节点执行后自动保存状态快照
- 按 `threadId` 隔离多用户会话
- 支持从历史检查点**恢复执行**（断点续跑）

---

## 包结构

```
org.venus.octopus.core
├── state/
│   └── MapAgentState.java          # 默认状态实现
├── graph/
│   ├── StateGraph.java             # 图构建器（核心入口）
│   ├── GraphNode.java              # 节点包装
│   ├── GraphEdge.java              # 边描述
│   ├── GraphDefinition.java        # 图结构定义
│   └── CompiledGraphImpl.java      # 编译后图实现
├── runner/
│   └── GraphRunner.java            # 图运行引擎
└── checkpoint/
    ├── Checkpoint.java             # 检查点接口
    └── InMemoryCheckpoint.java     # 内存检查点实现
```

---

## 依赖关系

```
octopus-api（接口层）
    ↑
octopus-common（工具层）
    ↑
octopus-core（本模块）
```

---

## 设计亮点

| 特性 | 说明 |
|---|---|
| **Builder 模式** | `StateGraph` 使用流式 Builder，API 简洁直观 |
| **状态不可变性** | 节点不直接修改传入状态，而是返回新的状态片段 |
| **函数式节点** | 节点可以是 Lambda 表达式，无需实现繁琐接口 |
| **条件路由** | `addConditionalEdges` 支持动态路由，实现 Agent 自主决策 |
| **可观测性** | 流式执行模式可逐节点观察中间状态 |
