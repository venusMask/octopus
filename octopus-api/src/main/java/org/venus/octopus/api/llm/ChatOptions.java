package org.venus.octopus.api.llm;

import java.util.ArrayList;
import java.util.List;

/**
 * 大模型调用参数配置
 * <p>
 * 统一定义温度、Top-P、生成长度等不同厂商基本都会支持的超参数，抹平各家 API 差异。
 * </p>
 */
public class ChatOptions {

    private Double temperature;
    private Double topP;
    private Integer maxTokens;
    private List<String> stopSequences;
    // 允许厂商特有的配置透传
    private String modelName;

    private ChatOptions(Builder builder) {
        this.temperature = builder.temperature;
        this.topP = builder.topP;
        this.maxTokens = builder.maxTokens;
        this.stopSequences = builder.stopSequences != null ? new ArrayList<>(builder.stopSequences) : null;
        this.modelName = builder.modelName;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public List<String> getStopSequences() {
        return stopSequences;
    }

    public String getModelName() {
        return modelName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Double temperature;
        private Double topP;
        private Integer maxTokens;
        private List<String> stopSequences;
        private String modelName;

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder topP(Double topP) {
            this.topP = topP;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder stopSequences(List<String> stopSequences) {
            this.stopSequences = stopSequences;
            return this;
        }

        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public ChatOptions build() {
            return new ChatOptions(this);
        }
    }
}
