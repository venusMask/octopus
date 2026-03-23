package org.venus.octopus.common.config;

import java.util.Optional;

/**
 * A read-only view of a configuration.
 */
public interface ReadableConfig {

    /**
     * Reads the given configuration option based on its key.
     */
    <T> T get(ConfigOption<T> option);

    /**
     * Reads the given configuration option as an {@link Optional}.
     */
    <T> Optional<T> getOptional(ConfigOption<T> option);

}
