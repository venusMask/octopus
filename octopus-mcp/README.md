# octopus-mcp — Model Context Protocol 支持

## 模块简介

`octopus-mcp` 为 Octopus 框架提供 **Model Context Protocol（MCP）** 的集成支持。MCP 是 Anthropic 提出的开放标准协议，定义了 AI 模型与外部工具/数据源之间的标准通信方式。

通过该模块，Octopus Agent 可以：
- 作为 **MCP Client**，连接到任意 MCP Server（如文件系统、数据库、外部服务）
- 作为 **MCP Server**，将 Agent 能力暴露给其他 MCP 兼容系统

---

## 核心功能

### 1. MCP 客户端（Client）

连接到 MCP Server，发现并调用其提供的工具：

```java
McpClient client = McpClient.builder()
    .serverUri("stdio://path/to/mcp-server")
    .build();

// 发现服务端工具
List<ToolSpec> tools = client.listTools();

// 调用服务端工具
String result = client.callTool("read_file", Map.of("path", "/tmp/data.txt"));
```

### 2. MCP 服务端（Server）

将 Octopus Tool 暴露为 MCP 兼容服务：

```java
McpServer server = McpServer.builder()
    .name("octopus-agent")
    .version("0.1")
    .tool(new WebSearchTool())
    .tool(new CalculatorTool())
    .build();

server.start(); // 启动 stdio 或 SSE 传输
```

### 3. MCP 工具适配器

`McpToolAdapter` 将 MCP Server 的工具自动适配为 Octopus `Tool` 接口，可直接注册到 `ToolRegistry` 中使用：

```java
McpToolAdapter adapter = new McpToolAdapter(mcpClient);
toolRegistry.registerAll(adapter.getTools());
```

### 4. 传输层支持

| 传输方式 | 说明 |
|---|---|
| `StdioTransport` | 标准输入输出（适合本地子进程）|
| `SseTransport` | Server-Sent Events（适合远程 HTTP）|

---

## 包结构

```
org.venus.octopus.mcp
├── client/
│   ├── McpClient.java              # MCP 客户端
│   └── McpToolAdapter.java         # 工具适配器（MCP→Octopus Tool）
├── server/
│   └── McpServer.java              # MCP 服务端
├── transport/
│   ├── McpTransport.java           # 传输层接口
│   ├── StdioTransport.java         # stdio 传输
│   └── SseTransport.java           # SSE 传输
└── protocol/
    ├── McpRequest.java             # MCP 请求
    └── McpResponse.java            # MCP 响应
```

---

## 依赖关系

```
octopus-api + octopus-common
    ↑
octopus-mcp（本模块）
```

---

## 参考协议

- [Model Context Protocol 官网](https://modelcontextprotocol.io)
- [MCP 规范](https://spec.modelcontextprotocol.io)
