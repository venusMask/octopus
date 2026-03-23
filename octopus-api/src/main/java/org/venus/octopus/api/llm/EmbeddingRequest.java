package org.venus.octopus.api.llm;

import org.venus.octopus.common.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;

public class EmbeddingRequest {

    private final List<String> inputs;
    private final String modelName;

    private EmbeddingRequest(Builder builder) {
        AssertUtils.notEmpty(builder.inputs, "Inputs cannot be empty for embedding request");
        this.inputs = new ArrayList<>(builder.inputs);
        this.modelName = builder.modelName;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public String getModelName() {
        return modelName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> inputs;
        private String modelName;

        public Builder inputs(List<String> inputs) {
            this.inputs = inputs;
            return this;
        }

        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public EmbeddingRequest build() {
            return new EmbeddingRequest(this);
        }
    }
}
