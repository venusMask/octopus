# Octopus 架构总览与逻辑边界

Octopus 是一个类 LangGraph 的 Java 版 Agent 图流程编排框架。项目主要分为以下模块，采用倒金字塔的物理依赖树：

- **`octopus-common`**: 基础公共设施层。包含全局的 `OctopusException`、单例安全封装的 `JsonUtils`，供多工程横向共享引用。
- **`octopus-api`**: 核心抽象防腐层。定义了抽象层面的 `Graph`、`Node`、`Message`、`Model` 族谱、`ChatMemory`、`Tool`。不包含具体业务逻辑，其他所有模块均直接依赖或间接依赖此包。
- **`octopus-memory`**: 上下文与对话记忆管理模块。包含了 `InMemoryChatMemory`，负责保存会话数据并且具备基于滑动窗口的剔除算法，用于防止 LLM 调用时 Token 超限。
- **`octopus-core`**: Agent Core 图形运转引擎。实现了基于状态机的 `GraphRunner`、默认的状态集 `MapAgentState`、专门捕获并保留历史消息记录的扩展 `MessagesState` 以及负责智能匹配函数调用指令并触发安全执行逻辑的 `ToolExecutor` 和 `ToolNode`。
- **`octopus-plugins`**: 大模型接口工厂与驱动执行层。目前内置了对 `OpenAI` JSON 协议的完全兼容，涵盖 `OpenAiChatModel` 与 `OpenAiEmbeddingModel`，在内部均利用纯正的 JDK 原生 `HttpClient` 实现极速异步转发，并且通过 Java 原生 SPI（`ServiceLoader`）将实例对外解耦暴露。
- **`octopus-mcp`**: （占位探索期）实现 Model Context Protocol (MCP) 服务器/客户端支持的独立模块。
- **`octopus-examples`**: （案例指引层）演示具体应用，指导开发人员在生产中对接。
