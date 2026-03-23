package org.venus.octopus.common.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationTest {

    private static final ConfigOption<String> STR_OPT = ConfigOptions.key("test.str").stringType()
            .defaultValue("default").withDescription("");
    private static final ConfigOption<Integer> INT_OPT = ConfigOptions.key("test.int").intType().defaultValue(42)
            .withDescription("");
    private static final ConfigOption<Boolean> BOOL_OPT = ConfigOptions.key("test.bool").booleanType()
            .defaultValue(true).withDescription("");

    enum MyEnum {
        A, B
    }
    private static final ConfigOption<MyEnum> ENUM_OPT = ConfigOptions.key("test.enum").enumType(MyEnum.class)
            .defaultValue(MyEnum.A).withDescription("");

    @Test
    void testDefaultValues() {
        Configuration config = new Configuration();
        assertEquals("default", config.get(STR_OPT));
        assertEquals(42, config.get(INT_OPT));
        assertTrue(config.get(BOOL_OPT));
        assertEquals(MyEnum.A, config.get(ENUM_OPT));
    }

    @Test
    void testSetAndGet() {
        Configuration config = new Configuration().set(STR_OPT, "newVal").set(INT_OPT, 100).set(BOOL_OPT, false)
                .set(ENUM_OPT, MyEnum.B);

        assertEquals("newVal", config.get(STR_OPT));
        assertEquals(100, config.get(INT_OPT));
        assertEquals(false, config.get(BOOL_OPT));
        assertEquals(MyEnum.B, config.get(ENUM_OPT));
    }
}
