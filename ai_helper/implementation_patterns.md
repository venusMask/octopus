# 项目代码级实现规范约束

在你参与 Octopus 下属模块研发时，大模型应该自动遵从以下核心技术惯例与选型禁忌：

## 1. 零第三方 HTTP/通讯依赖
所有的核心框架必须尽可能不产生不可控的第三方依赖。在 `octopus-plugins` 执行所有针对各家厂商大模型的公网或内网调用时，**必须且仅允许采用 Java 11 以后引入的 JDK 原生 `java.net.http.HttpClient`**，杜绝引入 OkHttp 或 Apache HttpClient。

## 2. JSON 的全局一致性使用
项目中已引入高版本的 `Jackson` 处理 JSON 序列化。
但在业务代码（或插件通讯层）中执行字符串转换时，**必须**使用挂载在 `octopus-common` 中的静态工具：`org.venus.octopus.common.utils.JsonUtils`。
它已内部隔离式地封装并开启了：JDK8 DateTime 支持、防止未知属性（FAIL_ON_UNKNOWN_PROPERTIES）崩溃功能和剔除 Null 值空跑（NON_NULL）。

## 3. Provider SPI 注入注册纪律
只要是模型提供商的功能对接层（例如你未来新增 Moonshot 或 DeepSeek 或自建大模型能力），严禁在业务引擎中直接直接 import 和强引用 `XxxModel(apiKey)` 实例。
1. 建立基于 `ChatModelFactory` / `EmbeddingModelFactory` 的工厂类。
2. 内部配置缺省值。
3. 在 `META-INF/services/org.venus.octopus.api.llm.XxxxModelFactory` 中使用全限定类名注册将该新工厂暴露给体系大动脉。

## 4. 异常抛出体系
任何由于各种底层序列化或底层 IO 生成的 Exception，应当第一时间在触发点（如 `HttpClient.send` 外包囊 try catch）捕获拦截。
禁止将框架底层的受检异常抛入 Graph 的引擎流转主干，而是应当包裹转译为项目域异常级别的 **`OctopusException`**。
