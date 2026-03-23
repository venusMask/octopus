package org.venus.octopus.common.exception;

/**
 * Base runtime exception for the Octopus framework.
 * <p>
 * All framework internal exceptions inherit from this class for unified
 * handling.
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
