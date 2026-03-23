package org.venus.octopus.common.exception;

/**
 * 节点执行异常，包含节点名称信息
 */
public class NodeException extends OctopusException {

    private final String nodeName;

    public NodeException(String nodeName, String message) {
        super("[节点:" + nodeName + "] " + message);
        this.nodeName = nodeName;
    }

    public NodeException(String nodeName, String message, Throwable cause) {
        super("[节点:" + nodeName + "] " + message, cause);
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }
}
