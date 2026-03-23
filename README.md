# Octopus — Java 版 Agent 框架

> 支持类 LangGraph 图流程编排的企业级 Java Agent 框架

---

## 简介

**Octopus** 是一个基于 Java 17 的 Agent（智能体）框架，核心设计灵感来自 Python 生态的 [LangGraph](https://github.com/langchain-ai/langgraph)。它允许开发者通过**有向图（DAG/循环图）**的方式编排 Agent 的执行流程，支持：

- 🔗 **图流程编排**：以节点（Node）+ 边（Edge）建模复杂 AI 工作流
- 🔄 **循环执行**：支持 Agent → Tools → Agent 的 ReAct 循环模式
- 🧠 **状态管理**：类型安全的 AgentState 在节点间传递和积累
- 💾 **记忆支持**：可插拔的长/短期记忆模块
- 🔌 **MCP 协议**：内置 Model Context Protocol 支持
- 🛠️ **插件扩展**：通过 Plugin 机制接入各种工具和 LLM

---

## 模块结构

```
octopus
├── octopus-api          # 接口与SPI定义层（Node、Graph、Tool等核心接口）
├── octopus-common       # 公共工具包（异常、工具类、注解）
├── octopus-core         # 核心引擎实现（图构建、编译、运行）
├── octopus-memory       # 记忆与上下文管理
├── octopus-mcp          # Model Context Protocol 支持
├── octopus-plugins      # 插件与工具扩展
├── octopus-examples     # 示例代码与最佳实践
└── octopus-test         # 测试基础设施
```

---

## 快速开始

### 基本用法示例

```java
// 构建一个 Agent 图
StateGraph<MapAgentState> graph = new StateGraph<>(MapAgentState::new)
    .addNode("agent", state -> callLLM(state))
    .addNode("tools", state -> executeTools(state))
    .addEdge(Graph.START, "agent")
    .addConditionalEdges(
        "agent",
        state -> (String) state.get("next"),   // 路由函数
        Map.of("tools", "tools", "end", Graph.END)
    )
    .addEdge("tools", "agent");

// 编译图
CompiledGraph<MapAgentState> compiled = graph.compile();

// 执行图
MapAgentState initialState = new MapAgentState();
initialState.put("messages", List.of(new HumanMessage("你好，今天天气如何？")));
MapAgentState result = compiled.invoke(initialState);
```

---

## 模块依赖关系

```
octopus-api
    ↑
octopus-common
    ↑
octopus-core → octopus-memory
    ↑
octopus-mcp, octopus-plugins
    ↑
octopus-examples
```

---

## 技术要求

- Java 17+
- Maven 3.8+

---

## 开源协议

Apache License 2.0
