package org.venus.octopus.plugins.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.octopus.api.llm.EmbeddingModel;
import org.venus.octopus.api.llm.EmbeddingRequest;
import org.venus.octopus.api.llm.EmbeddingResponse;
import org.venus.octopus.api.llm.TokenUsage;
import org.venus.octopus.common.exception.OctopusException;
import org.venus.octopus.common.utils.AssertUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OpenAiEmbeddingModel implements EmbeddingModel {

    private static final Logger log = LoggerFactory.getLogger(OpenAiEmbeddingModel.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String apiKey;
    private final String baseUrl;
    private final String defaultModel;
    private final HttpClient httpClient;

    public OpenAiEmbeddingModel(String apiKey, String baseUrl, String defaultModel) {
        AssertUtils.notEmpty(apiKey, "API Key is required");
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.defaultModel = defaultModel;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        String endpoint = baseUrl + "embeddings";

        ObjectNode requestBody = MAPPER.createObjectNode();
        String model = (request.getModelName() != null) ? request.getModelName() : defaultModel;
        requestBody.put("model", model);

        ArrayNode inputArray = requestBody.putArray("input");
        for (String i : request.getInputs()) {
            inputArray.add(i);
        }

        try {
            String jsonPayload = MAPPER.writeValueAsString(requestBody);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(endpoint))
                    .header("Content-Type", "application/json").header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();

            HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.error("Embedding API Error: {}", response.body());
                throw new OctopusException("Embedding Request failed: " + response.body());
            }

            return parseResponse(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OctopusException("Embedding request interrupted", e);
        } catch (Exception e) {
            throw new OctopusException("Error computing embeddings", e);
        }
    }

    private EmbeddingResponse parseResponse(String json) throws Exception {
        JsonNode root = MAPPER.readTree(json);
        JsonNode dataArray = root.path("data");
        if (!dataArray.isArray()) {
            throw new OctopusException("Missing 'data' in embedding response");
        }

        List<List<Double>> embeddings = new ArrayList<>();
        for (JsonNode dataObj : dataArray) {
            JsonNode embArray = dataObj.path("embedding");
            List<Double> vector = new ArrayList<>();
            for (JsonNode val : embArray) {
                vector.add(val.asDouble());
            }
            embeddings.add(vector);
        }

        JsonNode usageNode = root.path("usage");
        TokenUsage usage = TokenUsage.EMPTY;
        if (!usageNode.isMissingNode()) {
            usage = new TokenUsage(usageNode.path("prompt_tokens").asInt(0), 0,
                    usageNode.path("total_tokens").asInt(0));
        }

        return new EmbeddingResponse(embeddings, usage);
    }
}
