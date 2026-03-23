# octopus-examples — 示例代码与最佳实践

## 模块简介

`octopus-examples` 提供 Octopus 框架的**完整使用示例**，涵盖从简单入门到复杂多 Agent 协作场景，是学习和参考框架用法的最佳入口。

**注意**：此模块不应被其他模块依赖，仅作为演示和学习使用。

---

## 示例列表

### 1. 基础示例

#### `SimpleGraphExample` — Hello Graph
最简单的图流程示例，演示节点和边的基本用法：

```java
StateGraph<MapAgentState> graph = new StateGraph<>(MapAgentState::new)
    .addNode("greet", state -> {
        state.put("response", "Hello, " + state.get("name") + "!");
        return state;
    })
    .addEdge(Graph.START, "greet")
    .addEdge("greet", Graph.END);

CompiledGraph<MapAgentState> app = graph.compile();
MapAgentState result = app.invoke(MapAgentState.of("name", "World"));
System.out.println(result.get("response")); // Hello, World!
```

### 2. ReAct Agent 示例

#### `ReActAgentExample` — 带工具调用的 Agent
模拟 LLM 通过 ReAct（推理+行动）模式使用工具：

- 节点：`agent`（调用 LLM）→ `tools`（执行工具）→ `agent`（继续）
- 条件边：根据 LLM 是否请求工具调用决定是继续还是结束

### 3. 记忆 Agent 示例

#### `MemoryAgentExample` — 带持久化记忆
演示如何将 `octopus-memory` 模块集成到图中：

- 每次对话自动加载历史记忆
- 支持多用户会话隔离

### 4. 多 Agent 协作示例

#### `MultiAgentExample` — 多 Agent 协作
演示多个专业化 Agent 协作完成复杂任务：
- `supervisor` 节点：任务分发
- `researcher` 节点：信息检索
- `writer` 节点：内容生成

### 5. 检查点示例

#### `CheckpointExample` — 状态持久化
演示如何使用检查点实现：
- 长时运行任务的中断和恢复
- 多轮对话的状态保存

---

## 包结构

```
org.venus.octopus.examples
├── basic/
│   ├── SimpleGraphExample.java     # 基础图示例
│   └── ConditionalEdgeExample.java # 条件边示例
├── agent/
│   ├── ReActAgentExample.java      # ReAct Agent
│   └── MemoryAgentExample.java     # 记忆 Agent
├── multiagent/
│   └── MultiAgentExample.java      # 多 Agent 协作
└── checkpoint/
    └── CheckpointExample.java      # 检查点示例
```

---

## 运行示例

```bash
# 编译整个项目
cd /path/to/octopus
mvn compile -pl octopus-examples --also-make

# 运行具体示例（替换为对应类名）
mvn exec:java -pl octopus-examples \
  -Dexec.mainClass="org.venus.octopus.examples.basic.SimpleGraphExample"
```

---

## 依赖关系

```
octopus-api + octopus-core + octopus-memory
    ↑
octopus-examples（本模块，仅演示用，不被其他模块依赖）
```
