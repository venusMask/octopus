# 核心业务抽象与运行机制

## Graph 与状态机机制 (StateGraph)
Octopus 采用有向图（Graph）和代理状态机来实现大模型多步骤执行调度：
- **`StateGraph`**：面向开发者的图构建器。通过注册起始节点、拦截节点、结束节点（`Graph.END`），以及路由选择算法（`addConditionalEdges`）实现图论模型表达。
- **`GraphRunner`**：底层引擎执行器。基于图定义和初始化时的 `AgentState` 进行死循环调度，自动计算边路由切换节点，直到流程抵碰 `Graph.END` 节点退出死循环栈。
- **`MessagesState`**：特化状态对象容器，针对 `list` 等集合字段支持了基于 `reducer` 追加式拼接（而非全量覆盖合并），完美贴合长对话列表累加场景。

## 泛化的模型体系 (Model Hierarchy)
框架严禁出现“将大模型等价于对话模型”的设计，在 `octopus-api` 中：
- `Model<TReq, TRes>` 作为一切模型的共同基座，输入输出必须被结构实体化。
- **对话衍生**：使用 `ChatModel extends Model<ChatRequest, ChatResponse>`，参数封装于 `ChatRequest`，涵盖 Messages, Options 以及安全范围的工具 `Tools`。
- **词嵌入衍生**：使用 `EmbeddingModel extends Model<EmbeddingRequest, EmbeddingResponse>` 获取向量编码。
*提示：所有模型客户端不得在业务代码直接 `new` 实例化，需通过 SPI 工厂获取（如 `ChatModels.create("openai", map)`）。*

## 工具调度设计 (Tools Engine)
- API 层抽象了 `ToolSpec` JSON Schema 定义。
- `ToolExecutor` 支持在沙盒中独立且安全地剥离出大返回文本里夹带的并列式子任务 `ToolCall`，执行并组装成带上下文 ID 的 `ToolMessage` 汇入至后续编排流程当中。
