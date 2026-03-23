package org.venus.octopus.core.prompt;

import org.venus.octopus.common.utils.AssertUtils;

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

    public PromptTemplate(String template) {
        AssertUtils.notNull(template, "模板内容不能为 null");
        this.template = template;
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
            Object value = variables.get(varName);
            // 若变量不存在，可选择保留原样，或替换为空串。此处选择替换为空串并严格记录或略过。
            // 为了兼顾容错性，如果不包含该 key，保留原样
            if (variables.containsKey(varName)) {
                matcher.appendReplacement(result, value == null ? "" : Matcher.quoteReplacement(value.toString()));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement("{" + varName + "}"));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public String getTemplate() {
        return template;
    }
}
