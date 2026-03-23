package org.venus.octopus.common.exception;

/**
 * 图构建或执行过程中的异常
 */
public class GraphException extends OctopusException {

    public GraphException(String message) {
        super(message);
    }

    public GraphException(String message, Throwable cause) {
        super(message, cause);
    }
}
