# octopus-common — 公共工具包

## 模块简介

`octopus-common` 是 Octopus 框架的**公共基础包**，提供各模块共用的工具类、异常体系和注解。该模块是 Octopus 框架依赖链的最底层，不依赖任何其他框架内部模块。

---

## 核心功能

### 1. 异常体系（`exception` 包）

统一的异常类层次结构，所有框架异常均继承自 `OctopusException`：

| 异常类 | 说明 |
|---|---|
| `OctopusException` | 框架基础运行时异常 |
| `GraphException` | 图构建或执行过程中的异常 |
| `NodeException` | 节点执行异常（包含节点名称信息） |
| `StateException` | 状态操作异常 |
| `ToolException` | 工具调用异常 |

**使用示例**：
```java
throw new GraphException("节点 '" + nodeName + "' 未找到");
throw new NodeException("agent", "节点执行超时", cause);
```

### 2. 工具类（`utils` 包）

| 工具类 | 核心方法 | 说明 |
|---|---|---|
| `StringUtils` | `isEmpty`, `isBlank`, `truncate` | 字符串处理工具 |
| `AssertUtils` | `notNull`, `notEmpty`, `isTrue` | 参数断言，失败时抛出具体异常 |
| `CollectionUtils` | `isEmpty`, `firstOrNull` | 集合操作工具 |

### 3. 注解（`annotation` 包）

| 注解 | 说明 |
|---|---|
| `@NodeComponent` | 标注一个类为图节点组件，支持按名称注册 |

---

## 包结构

```
org.venus.octopus.common
├── exception/
│   ├── OctopusException.java       # 框架基础异常
│   ├── GraphException.java         # 图异常
│   ├── NodeException.java          # 节点异常
│   ├── StateException.java         # 状态异常
│   └── ToolException.java          # 工具异常
├── utils/
│   ├── StringUtils.java            # 字符串工具
│   ├── AssertUtils.java            # 断言工具
│   └── CollectionUtils.java        # 集合工具
└── annotation/
    └── NodeComponent.java          # 节点组件注解
```

---

## 依赖关系

```
octopus-common（本模块，框架依赖链最底层，无框架内部依赖）
    ↑
  被 octopus-api、octopus-core 等所有模块依赖
```
