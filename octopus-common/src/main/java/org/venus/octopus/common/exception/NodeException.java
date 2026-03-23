package org.venus.octopus.common.exception;

/**
 * Exception during node execution, including the node name.
 */
public class NodeException extends OctopusException {

    private final String nodeName;

    public NodeException(String nodeName, String message) {
        super("[Node:" + nodeName + "] " + message);
        this.nodeName = nodeName;
    }

    public NodeException(String nodeName, String message, Throwable cause) {
        super("[Node:" + nodeName + "] " + message, cause);
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }
}
