package org.venus.octopus.core.tool;

import org.venus.octopus.api.tool.Tool;
import org.venus.octopus.common.exception.ToolException;
import org.venus.octopus.common.utils.AssertUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standalone tool registry.
 * <p>
 * Used to uniformly manage and maintain all available tools (Tools) in the
 * system or session. It can be used in regular LLM calls or bound to the Graph
 * runtime.
 * </p>
 */
public class ToolRegistry {

    private final Map<String, Tool> toolMap = new ConcurrentHashMap<>();

    /**
     * Registers a single tool.
     *
     * @param tool
     *            The tool instance
     */
    public void register(Tool tool) {
        AssertUtils.notNull(tool, "Tool cannot be null");
        String name = tool.getName();
        AssertUtils.notEmpty(name, "Tool name cannot be empty");
        if (toolMap.containsKey(name)) {
            throw new ToolException(name, "Tool name conflict, tool with name '" + name + "' already exists");
        }
        toolMap.put(name, tool);
    }

    /**
     * Registers tools in batch.
     *
     * @param tools
     *            The collection of tools
     */
    public void registerAll(Collection<Tool> tools) {
        if (tools != null) {
            tools.forEach(this::register);
        }
    }

    /**
     * Gets a tool by name.
     *
     * @param name
     *            The tool name
     * @return The tool instance, or null if it does not exist
     */
    public Tool getTool(String name) {
        return toolMap.get(name);
    }

    /**
     * Gets all registered tools.
     */
    public Collection<Tool> getAllTools() {
        return Collections.unmodifiableCollection(toolMap.values());
    }
}
