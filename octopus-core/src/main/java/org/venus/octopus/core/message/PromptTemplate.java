package org.venus.octopus.core.message;

import org.venus.octopus.common.config.Configuration;
import org.venus.octopus.common.config.ReadableConfig;
import org.venus.octopus.common.utils.AssertUtils;
import org.venus.octopus.core.message.config.MissingVariablePolicy;
import org.venus.octopus.core.message.config.PromptOptions;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Prompt template.
 * <p>
 * Supports simple {@code {variable}} placeholder substitution. Independent of
 * specific graph logic, it can be used for data preparation before any model
 * call.
 * </p>
 */
public class PromptTemplate {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");

    private final String template;
    private final ReadableConfig config;

    public PromptTemplate(String template) {
        this(template, new Configuration());
    }

    public PromptTemplate(String template, ReadableConfig config) {
        AssertUtils.notNull(template, "Template content cannot be null");
        AssertUtils.notNull(config, "Configuration cannot be null");
        this.template = template;
        this.config = config;
    }

    /**
     * Renders a variable Map into the template.
     *
     * @param variables
     *            The variable dictionary
     * @return The rendered string
     */
    public String format(Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1);

            if (variables.containsKey(varName)) {
                Object value = variables.get(varName);
                matcher.appendReplacement(result, value == null ? "" : Matcher.quoteReplacement(value.toString()));
            } else {
                MissingVariablePolicy policy = config.get(PromptOptions.MISSING_VARIABLE_POLICY);
                if (policy == MissingVariablePolicy.REPLACE_EMPTY) {
                    matcher.appendReplacement(result, "");
                } else if (policy == MissingVariablePolicy.THROW_EXCEPTION) {
                    throw new IllegalArgumentException("Template is missing required variable injection: " + varName);
                } else {
                    // 默认行为：KEEP_ORIGINAL
                    matcher.appendReplacement(result, Matcher.quoteReplacement("{" + varName + "}"));
                }
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public String getTemplate() {
        return template;
    }
}
