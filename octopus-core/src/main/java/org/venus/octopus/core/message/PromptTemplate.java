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
 * 提示词模板
 * <p>
 * 支持简单的 {@code {variable}} 占位符替换。 独立于具体的图逻辑，可通用于任何模型调用前的数据准备。
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
        AssertUtils.notNull(template, "模板内容不能为 null");
        AssertUtils.notNull(config, "配置不能为 null");
        this.template = template;
        this.config = config;
    }

    /**
     * 将变量 Map 渲染到模板中
     *
     * @param variables
     *            变量字典
     * @return 渲染后的字符串
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
                    throw new IllegalArgumentException("模板缺少必要的变量注入: " + varName);
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
