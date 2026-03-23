package org.venus.octopus.api.llm;

import java.util.Map;

/**
 * 嵌入(Embedding)模型的 SPI 工厂定义
 */
public interface EmbeddingModelFactory {

    String providerName();

    EmbeddingModel createModel(Map<String, Object> properties);
}
