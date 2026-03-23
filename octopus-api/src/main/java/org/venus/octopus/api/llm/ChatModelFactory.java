package org.venus.octopus.api.llm;

import java.util.Map;

/**
 * SPI 的服务端点暴露工厂
 * <p>
 * 通过 Java {@link java.util.ServiceLoader} 自动加载实现此接口的类。 各大 LLM 插件（例如
 * openai-plugin）应实现该工厂，并在其
 * `META-INF/services/org.venus.octopus.api.llm.ChatModelFactory` 中注册。
 * </p>
 */
public interface ChatModelFactory {

    /**
     * @return 该工厂对应的明确厂商名称标识，例如 "openai", "dashscope", "ollama" 等。忽略大小写匹配。
     */
    String providerName();

    /**
     * 依据外部配置属性构造模型客户端实例
     *
     * @param properties
     *            配置字典（例如 api_key, api_base 等信息）
     * @return 就绪的模型对象
     */
    ChatModel createModel(Map<String, Object> properties);
}
