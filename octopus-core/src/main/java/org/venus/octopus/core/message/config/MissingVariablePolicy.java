package org.venus.octopus.core.message.config;

/**
 * Defines the policy for handling missing variables in prompt templates.
 */
public enum MissingVariablePolicy {
    /**
     * Keep the original placeholder (e.g. "{varName}").
     */
    KEEP_ORIGINAL,

    /**
     * Replace the placeholder with an empty string.
     */
    REPLACE_EMPTY,

    /**
     * Throw an IllegalArgumentException.
     */
    THROW_EXCEPTION
}
