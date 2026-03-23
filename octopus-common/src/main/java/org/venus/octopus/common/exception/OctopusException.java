package org.venus.octopus.common.exception;

/**
 * Octopus 框架基础运行时异常
 * <p>
 * 所有框架内部异常均继承自本类，便于统一捕获处理。
 * </p>
 */
public class OctopusException extends RuntimeException {

    public OctopusException(String message) {
        super(message);
    }

    public OctopusException(String message, Throwable cause) {
        super(message, cause);
    }

    public OctopusException(Throwable cause) {
        super(cause);
    }
}
