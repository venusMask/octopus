package org.venus.octopus.core.tool;

import org.venus.octopus.api.tool.Tool;
import org.venus.octopus.common.exception.ToolException;
import org.venus.octopus.common.utils.AssertUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 独立的工具注册表
 * <p>
 * 用于统一管理和维护系统或会话中所有可用的被叫工具（Tools）。 可在常规的 LLM 调用中使用，也可被绑定至 Graph 运行时使用。
 * </p>
 */
public class ToolRegistry {

    private final Map<String, Tool> toolMap = new ConcurrentHashMap<>();

    /**
     * 注册单个工具
     *
     * @param tool
     *            工具实例
     */
    public void register(Tool tool) {
        AssertUtils.notNull(tool, "Tool 不能为 null");
        String name = tool.getName();
        AssertUtils.notEmpty(name, "Tool名称不能为空");
        if (toolMap.containsKey(name)) {
            throw new ToolException(name, "工具名称冲突，已存在名为 '" + name + "' 的工具");
        }
        toolMap.put(name, tool);
    }

    /**
     * 批量注册工具
     *
     * @param tools
     *            工具集合
     */
    public void registerAll(Collection<Tool> tools) {
        if (tools != null) {
            tools.forEach(this::register);
        }
    }

    /**
     * 根据名称获取工具
     *
     * @param name
     *            工具名
     * @return 工具实例，如果不存则返回 null
     */
    public Tool getTool(String name) {
        return toolMap.get(name);
    }

    /**
     * 获取所有已注册工具
     */
    public Collection<Tool> getAllTools() {
        return Collections.unmodifiableCollection(toolMap.values());
    }
}
