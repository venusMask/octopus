package org.venus.octopus.common.exception;

/**
 * Exception during graph construction or execution.
 */
public class GraphException extends OctopusException {

    public GraphException(String message) {
        super(message);
    }

    public GraphException(String message, Throwable cause) {
        super(message, cause);
    }
}
