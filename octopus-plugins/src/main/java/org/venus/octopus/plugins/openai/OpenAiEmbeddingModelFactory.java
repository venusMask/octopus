package org.venus.octopus.plugins.openai;

import org.venus.octopus.api.llm.EmbeddingModel;
import org.venus.octopus.api.llm.EmbeddingModelFactory;

import java.util.Map;

public class OpenAiEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public String providerName() {
        return "openai";
    }

    @Override
    public EmbeddingModel createModel(Map<String, Object> properties) {
        if (properties == null) {
            properties = Map.of();
        }

        String apiKey = (String) properties.get("api_key");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("OPENAI_API_KEY");
        }

        String baseUrl = (String) properties.get("base_url");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://api.openai.com/v1/";
        }

        String defaultModel = (String) properties.get("model");
        if (defaultModel == null || defaultModel.isBlank()) {
            defaultModel = "text-embedding-ada-002";
        }

        return new OpenAiEmbeddingModel(apiKey, baseUrl, defaultModel);
    }
}
