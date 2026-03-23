package org.venus.octopus.plugins.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.octopus.api.llm.ChatModel;
import org.venus.octopus.api.llm.ChatOptions;
import org.venus.octopus.api.llm.ChatRequest;
import org.venus.octopus.api.llm.ChatResponse;
import org.venus.octopus.api.llm.TokenUsage;
import org.venus.octopus.api.message.*;
import org.venus.octopus.api.tool.ToolSpec;
import org.venus.octopus.common.exception.OctopusException;
import org.venus.octopus.common.utils.AssertUtils;
import org.venus.octopus.common.utils.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class OpenAiChatModel implements ChatModel {

    private static final Logger log = LoggerFactory.getLogger(OpenAiChatModel.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String apiKey;
    private final String baseUrl;
    private final String defaultModel;
    private final HttpClient httpClient;

    public OpenAiChatModel(String apiKey, String baseUrl, String defaultModel) {
        AssertUtils.notEmpty(apiKey, "OpenAI API Key must be provided (either via properties or env)");
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.defaultModel = defaultModel;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    @Override
    public ChatResponse call(ChatRequest request) {
        List<Message> messages = request.getMessages();
        ChatOptions options = request.getOptions();
        List<ToolSpec> tools = request.getTools();

        String endpoint = baseUrl + "chat/completions";

        ObjectNode requestBody = MAPPER.createObjectNode();
        String model = (options != null && options.getModelName() != null) ? options.getModelName() : defaultModel;
        requestBody.put("model", model);

        // 设置可选参数
        if (options != null) {
            if (options.getTemperature() != null)
                requestBody.put("temperature", options.getTemperature());
            if (options.getTopP() != null)
                requestBody.put("top_p", options.getTopP());
            if (options.getMaxTokens() != null)
                requestBody.put("max_tokens", options.getMaxTokens());
        }

        // 构建消息体
        ArrayNode messagesArray = requestBody.putArray("messages");
        for (Message msg : messages) {
            ObjectNode msgNode = messagesArray.addObject();
            if (msg instanceof SystemMessage) {
                msgNode.put("role", "system");
                msgNode.put("content", msg.getContent());
            } else if (msg instanceof HumanMessage) {
                msgNode.put("role", "user");
                msgNode.put("content", msg.getContent());
            } else if (msg instanceof AiMessage ai) {
                msgNode.put("role", "assistant");
                if (!ai.hasToolCalls()) {
                    msgNode.put("content", ai.getContent() == null ? "" : ai.getContent());
                } else {
                    // 支持工具调用请求的协议结构组装
                    msgNode.put("content", StringUtils.isEmpty(ai.getContent()) ? "" : ai.getContent());
                    ArrayNode toolCallsArray = msgNode.putArray("tool_calls");
                    for (AiMessage.ToolCall call : ai.getToolCalls()) {
                        ObjectNode callNode = toolCallsArray.addObject();
                        callNode.put("id", call.id());
                        callNode.put("type", "function");
                        ObjectNode functionNode = callNode.putObject("function");
                        functionNode.put("name", call.toolName());
                        try {
                            functionNode.put("arguments", MAPPER.writeValueAsString(call.arguments()));
                        } catch (Exception e) {
                            throw new OctopusException("Failed to serialize tool arguments", e);
                        }
                    }
                }
            } else if (msg instanceof ToolMessage toolMsg) {
                msgNode.put("role", "tool");
                msgNode.put("tool_call_id", toolMsg.getToolCallId());
                msgNode.put("content", toolMsg.getContent());
            }
        }

        // 解析并附带允许使用的外部工具 (Tool Definitions)
        if (tools != null && !tools.isEmpty()) {
            ArrayNode toolsArray = requestBody.putArray("tools");
            for (ToolSpec tool : tools) {
                ObjectNode toolNode = toolsArray.addObject();
                toolNode.put("type", "function");
                ObjectNode functionNode = toolNode.putObject("function");
                functionNode.put("name", tool.getName());
                functionNode.put("description", tool.getDescription());

                ObjectNode paramsNode = functionNode.putObject("parameters");
                paramsNode.put("type", "object");
                ObjectNode propertiesNode = paramsNode.putObject("properties");
                ArrayNode requiredArray = paramsNode.putArray("required");

                for (ToolSpec.ParameterSpec param : tool.getParameters()) {
                    ObjectNode pNode = propertiesNode.putObject(param.name());
                    pNode.put("type", param.type());
                    pNode.put("description", param.description());
                    if (param.required()) {
                        requiredArray.add(param.name());
                    }
                }
            }
        }

        try {
            String jsonPayload = MAPPER.writeValueAsString(requestBody);
            log.debug("Sending request to LLM ({}): {}", model, jsonPayload);

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(endpoint))
                    .header("Content-Type", "application/json").header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                log.error("LLM API Error: {}", response.body());
                throw new OctopusException(
                        "LLM Request failed with status " + response.statusCode() + ": " + response.body());
            }

            return parseResponse(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OctopusException("LLM request interrupted", e);
        } catch (Exception e) {
            throw new OctopusException("Error communicating with LLM provider", e);
        }
    }

    private ChatResponse parseResponse(String json) throws Exception {
        JsonNode rootNode = MAPPER.readTree(json);
        JsonNode choicesNode = rootNode.path("choices");
        if (choicesNode.isMissingNode() || !choicesNode.isArray() || choicesNode.size() == 0) {
            throw new OctopusException("Invalid LLM JSON response: missing 'choices'");
        }

        JsonNode messageNode = choicesNode.get(0).path("message");
        String content = messageNode.path("content").asText(null);

        // 检测 LLM 是否请求执行工具
        List<AiMessage.ToolCall> parsedToolCalls = new ArrayList<>();
        JsonNode toolCallsNode = messageNode.path("tool_calls");
        if (toolCallsNode.isArray()) {
            for (JsonNode callNode : toolCallsNode) {
                String id = callNode.path("id").asText();
                JsonNode functionNode = callNode.path("function");
                String name = functionNode.path("name").asText();
                String argsString = functionNode.path("arguments").asText();

                @SuppressWarnings("unchecked")
                Map<String, Object> arguments = MAPPER.readValue(argsString, HashMap.class);
                parsedToolCalls.add(new AiMessage.ToolCall(id, name, arguments));
            }
        }

        AiMessage finalMessage;
        if (parsedToolCalls.isEmpty()) {
            finalMessage = new AiMessage(content);
        } else {
            finalMessage = new AiMessage(content, parsedToolCalls);
        }

        // 解析 Token 消耗数据
        TokenUsage usage = TokenUsage.EMPTY;
        JsonNode usageNode = rootNode.path("usage");
        if (!usageNode.isMissingNode()) {
            usage = new TokenUsage(usageNode.path("prompt_tokens").asInt(0),
                    usageNode.path("completion_tokens").asInt(0), usageNode.path("total_tokens").asInt(0));
        }

        return new ChatResponse(finalMessage, usage);
    }
}
