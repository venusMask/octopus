# octopus-plugins — 插件与工具扩展

## 模块简介

`octopus-plugins` 提供 Octopus 框架的**工具（Tool）扩展机制**，允许开发者以标准化的方式将各种能力（搜索、计算、数据库、HTTP 请求等）注册为 Agent 可调用的工具。

该模块实现了 `octopus-api` 中的 `Tool` 和 `ToolSpec` 接口，并提供工具注册、发现和执行的基础设施。

---

## 核心功能

### 1. 工具注解驱动开发

通过 `@OctopusTool` 注解快速定义工具：

```java
@OctopusTool(name = "web_search", description = "搜索互联网获取最新信息")
public class WebSearchTool implements Tool {
    @Override
    public String execute(Map<String, Object> args) {
        String query = (String) args.get("query");
        // 执行搜索逻辑...
        return searchResult;
    }

    @Override
    public ToolSpec getSpec() {
        return ToolSpec.builder()
            .name("web_search")
            .description("搜索互联网获取最新信息")
            .parameter("query", "string", "搜索关键词", true)
            .build();
    }
}
```

### 2. 工具注册中心

`ToolRegistry` 管理所有可用工具，支持：
- 手动注册：`registry.register(new WebSearchTool())`
- 自动扫描：基于 `@OctopusTool` 注解自动发现
- 按名称查找：`registry.getTool("web_search")`
- 获取所有工具规格（用于 LLM function calling）

### 3. 内置工具

模块内置常用工具实现：

| 工具 | 说明 |
|---|---|
| `HttpRequestTool` | 发起 HTTP GET/POST 请求 |
| `JavaScriptTool` | 执行简单 JavaScript 表达式（Nashorn/GraalVM） |
| `TimeTool` | 获取当前时间 |
| `CalculatorTool` | 数学计算 |

### 4. 工具执行器

`ToolExecutor` 负责解析 LLM 返回的工具调用指令并路由到对应工具：

```java
// 典型用法（在 tools 节点中）
ToolExecutor executor = new ToolExecutor(registry);
MapAgentState toolsNode(MapAgentState state) {
    AiMessage aiMsg = (AiMessage) state.get("last_message");
    List<ToolMessage> results = executor.executeAll(aiMsg.getToolCalls());
    return state.with("tool_results", results);
}
```

---

## 包结构

```
org.venus.octopus.plugins
├── annotation/
│   └── OctopusTool.java            # 工具注解
├── registry/
│   ├── ToolRegistry.java           # 工具注册中心
│   └── DefaultToolRegistry.java    # 默认实现
├── executor/
│   └── ToolExecutor.java           # 工具执行器
└── builtin/
    ├── HttpRequestTool.java        # HTTP 工具
    ├── TimeTool.java               # 时间工具
    └── CalculatorTool.java         # 计算器工具
```

---

## 依赖关系

```
octopus-api + octopus-common
    ↑
octopus-plugins（本模块）
```
