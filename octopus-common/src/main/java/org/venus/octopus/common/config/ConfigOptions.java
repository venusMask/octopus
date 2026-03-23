package org.venus.octopus.common.config;

/**
 * A builder mechanism for creating {@link ConfigOption}.
 */
public class ConfigOptions {

    /**
     * Starts building a new {@link ConfigOption} with the given key.
     */
    public static OptionBuilder key(String key) {
        return new OptionBuilder(key);
    }

    public static class OptionBuilder {
        private final String key;

        OptionBuilder(String key) {
            this.key = key;
        }

        public <T> TypedConfigOptionBuilder<T> classType(Class<T> clazz) {
            return new TypedConfigOptionBuilder<>(key, clazz);
        }

        public TypedConfigOptionBuilder<String> stringType() {
            return new TypedConfigOptionBuilder<>(key, String.class);
        }

        public TypedConfigOptionBuilder<Boolean> booleanType() {
            return new TypedConfigOptionBuilder<>(key, Boolean.class);
        }

        public TypedConfigOptionBuilder<Integer> intType() {
            return new TypedConfigOptionBuilder<>(key, Integer.class);
        }

        public <T extends Enum<T>> TypedConfigOptionBuilder<T> enumType(Class<T> enumClass) {
            return new TypedConfigOptionBuilder<>(key, enumClass);
        }
    }

    public static class TypedConfigOptionBuilder<T> {
        private final String key;
        private final Class<T> clazz;
        private T defaultValue;

        TypedConfigOptionBuilder(String key, Class<T> clazz) {
            this.key = key;
            this.clazz = clazz;
        }

        public TypedConfigOptionBuilder<T> defaultValue(T value) {
            this.defaultValue = value;
            return this;
        }

        public ConfigOption<T> noDefaultValue() {
            return new ConfigOption<>(key, clazz, null, "");
        }

        public ConfigOption<T> withDescription(String description) {
            return new ConfigOption<>(key, clazz, defaultValue, description);
        }
    }
}
