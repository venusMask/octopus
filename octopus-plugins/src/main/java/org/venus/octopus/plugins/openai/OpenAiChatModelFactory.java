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

        // Extract API KEY, try reading from environment variables if not configured
        String apiKey = (String) properties.get("api_key");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("OPENAI_API_KEY");
        }

        // Extract baseUrl, default to official if not configured; most domestic LLMs
        // can override by passing baseUrl
        String baseUrl = (String) properties.get("base_url");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://api.openai.com/v1/";
        }

        // Default model name
        String defaultModel = (String) properties.get("model");
        if (defaultModel == null || defaultModel.isBlank()) {
            defaultModel = "gpt-3.5-turbo";
        }

        return new OpenAiChatModel(apiKey, baseUrl, defaultModel);
    }
}
