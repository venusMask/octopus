package org.venus.octopus.common.exception;

/**
 * 工具调用异常
 */
public class ToolException extends OctopusException {

    private final String toolName;

    public ToolException(String toolName, String message) {
        super("[工具:" + toolName + "] " + message);
        this.toolName = toolName;
    }

    public ToolException(String toolName, String message, Throwable cause) {
        super("[工具:" + toolName + "] " + message, cause);
        this.toolName = toolName;
    }

    public String getToolName() {
        return toolName;
    }
}
