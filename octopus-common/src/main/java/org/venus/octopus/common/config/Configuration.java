package org.venus.octopus.common.config;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The main configuration structure. Provides type-safe setting and reading of
 * {@link ConfigOption} properties.
 */
public class Configuration implements ReadableConfig {

    private final Map<String, Object> confData = new ConcurrentHashMap<>();

    /**
     * Set a typed config option with its value.
     */
    public <T> Configuration set(ConfigOption<T> option, T value) {
        if (value == null) {
            confData.remove(option.key());
        } else {
            confData.put(option.key(), value);
        }
        return this;
    }

    @Override
    public <T> T get(ConfigOption<T> option) {
        Object val = confData.get(option.key());
        if (val == null) {
            return option.defaultValue();
        }
        return convert(val, option.clazz());
    }

    @Override
    public <T> Optional<T> getOptional(ConfigOption<T> option) {
        Object val = confData.get(option.key());
        if (val == null) {
            return Optional.ofNullable(option.defaultValue());
        }
        return Optional.ofNullable(convert(val, option.clazz()));
    }

    /**
     * Merge all options from another configuration into this one.
     */
    public Configuration addAll(Configuration other) {
        if (other != null) {
            this.confData.putAll(other.confData);
        }
        return this;
    }

    public Map<String, Object> toMap() {
        return new ConcurrentHashMap<>(confData);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> T convert(Object value, Class<T> clazz) {
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (clazz.isEnum()) {
                try {
                    return (T) Enum.valueOf((Class<Enum>) clazz, strVal);
                } catch (IllegalArgumentException e) {
                    // Ignore enum parse failure safely, fallback to explicit cast which may throw
                    // ClassCastException
                }
            } else if (clazz == Integer.class || clazz == int.class) {
                return (T) Integer.valueOf(strVal);
            } else if (clazz == Boolean.class || clazz == boolean.class) {
                return (T) Boolean.valueOf(strVal);
            } else if (clazz == Long.class || clazz == long.class) {
                return (T) Long.valueOf(strVal);
            } else if (clazz == Double.class || clazz == double.class) {
                return (T) Double.valueOf(strVal);
            }
        }

        // Final fallback
        return (T) value;
    }

    @Override
    public String toString() {
        return confData.toString();
    }
}
