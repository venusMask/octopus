package org.venus.octopus.api.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tool specification description.
 * <p>
 * Describes the tool's metadata, used to generate function calling
 * configuration (JSON Schema style) for LLMs.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 *
 * <pre>{@code
 * ToolSpec spec = ToolSpec.builder().name("web_search").description("Search the internet for the latest information")
 *         .parameter("query", "string", "Search keywords", true).build();
 * }</pre>
 */
public class ToolSpec {

    private final String name;
    private final String description;
    private final List<ParameterSpec> parameters;

    private ToolSpec(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.parameters = Collections.unmodifiableList(new ArrayList<>(builder.parameters));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ParameterSpec> getParameters() {
        return parameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Tool parameter specification.
     *
     * @param name
     *            Name of the parameter
     * @param type
     *            Type of the parameter
     *            ("string"/"number"/"boolean"/"object"/"array")
     * @param description
     *            Description of the parameter
     * @param required
     *            Whether the parameter is required
     */
    public record ParameterSpec(String name, String type, String description, boolean required) {
    }

    /**
     * ToolSpec builder.
     */
    public static class Builder {
        private String name;
        private String description;
        private final List<ParameterSpec> parameters = new ArrayList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder parameter(String name, String type, String description, boolean required) {
            this.parameters.add(new ParameterSpec(name, type, description, required));
            return this;
        }

        public ToolSpec build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("ToolSpec name cannot be null or empty");
            }
            return new ToolSpec(this);
        }
    }
}
