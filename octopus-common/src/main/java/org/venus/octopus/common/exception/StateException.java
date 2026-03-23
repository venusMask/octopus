package org.venus.octopus.common.exception;

/**
 * 状态操作异常
 */
public class StateException extends OctopusException {

    public StateException(String message) {
        super(message);
    }

    public StateException(String message, Throwable cause) {
        super(message, cause);
    }
}
