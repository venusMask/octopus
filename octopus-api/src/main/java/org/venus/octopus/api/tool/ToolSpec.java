package org.venus.octopus.api.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 工具规格描述
 * <p>
 * 描述工具的元数据，用于向 LLM 生成 function calling 配置（JSON Schema 风格）。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * ToolSpec spec = ToolSpec.builder()
 *     .name("web_search")
 *     .description("搜索互联网获取最新信息")
 *     .parameter("query", "string", "搜索关键词", true)
 *     .build();
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
     * 工具参数规格
     *
     * @param name        参数名称
     * @param type        参数类型（"string"/"number"/"boolean"/"object"/"array"）
     * @param description 参数描述
     * @param required    是否必填
     */
    public record ParameterSpec(String name, String type, String description, boolean required) {}

    /**
     * ToolSpec 构建器
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
                throw new IllegalArgumentException("ToolSpec 的 name 不能为空");
            }
            return new ToolSpec(this);
        }
    }
}
