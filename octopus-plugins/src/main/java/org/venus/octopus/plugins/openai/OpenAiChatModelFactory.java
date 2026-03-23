package org.venus.octopus.plugins.openai;

import org.venus.octopus.api.llm.ChatModel;
import org.venus.octopus.api.llm.ChatModelFactory;

import java.util.Map;

public class OpenAiChatModelFactory implements ChatModelFactory {

    @Override
    public String providerName() {
        return "openai";
    }

    @Override
    public ChatModel createModel(Map<String, Object> properties) {
        if (properties == null) {
            properties = Map.of();
        }

        // 提取 API KEY，如果没配就尝试读环境变量
        String apiKey = (String) properties.get("api_key");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("OPENAI_API_KEY");
        }

        // 提取 baseUrl，如果没配默认官方的，多数国产大模型可通过传 baseUrl 覆盖
        String baseUrl = (String) properties.get("base_url");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://api.openai.com/v1/";
        }

        // 默认模型名
        String defaultModel = (String) properties.get("model");
        if (defaultModel == null || defaultModel.isBlank()) {
            defaultModel = "gpt-3.5-turbo";
        }

        return new OpenAiChatModel(apiKey, baseUrl, defaultModel);
    }
}
