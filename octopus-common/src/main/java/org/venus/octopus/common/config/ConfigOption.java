package org.venus.octopus.common.config;

import java.util.Objects;

/**
 * A typed configuration option. It represents a key-value setting with type,
 * default value, and description. Similar to Flink's ConfigOption.
 *
 * @param <T>
 *            The type of the value
 */
public class ConfigOption<T> {
    private final String key;
    private final Class<T> clazz;
    private final T defaultValue;
    private final String description;

    ConfigOption(String key, Class<T> clazz, T defaultValue, String description) {
        this.key = key;
        this.clazz = clazz;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public String key() {
        return key;
    }

    public Class<T> clazz() {
        return clazz;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public String description() {
        return description;
    }

    /**
     * Creates a new ConfigOption with the given description.
     */
    public ConfigOption<T> withDescription(String description) {
        return new ConfigOption<>(key, clazz, defaultValue, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ConfigOption<?> that = (ConfigOption<?>) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "ConfigOption{" + "key='" + key + '\'' + ", clazz=" + clazz + ", defaultValue=" + defaultValue + '}';
    }
}
