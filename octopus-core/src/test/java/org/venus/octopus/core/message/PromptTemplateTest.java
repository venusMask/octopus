package org.venus.octopus.core.message;

import org.junit.jupiter.api.Test;
import org.venus.octopus.common.config.Configuration;
import org.venus.octopus.core.message.config.MissingVariablePolicy;
import org.venus.octopus.core.message.config.PromptOptions;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PromptTemplateTest {

    @Test
    public void testFormatWithAllVariables() {
        PromptTemplate template = new PromptTemplate("Hello {name}, your age is {age}.");
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "Alice");
        vars.put("age", 30);
        assertEquals("Hello Alice, your age is 30.", template.format(vars));
    }

    @Test
    public void testMissingVariableKeepOriginal() {
        // Default behavior
        PromptTemplate template = new PromptTemplate("Hello {name}, your age is {age}.");
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "Alice"); // missing age
        assertEquals("Hello Alice, your age is {age}.", template.format(vars));
    }

    @Test
    public void testMissingVariableReplaceEmpty() {
        Configuration config = new Configuration().set(PromptOptions.MISSING_VARIABLE_POLICY,
                MissingVariablePolicy.REPLACE_EMPTY);
        PromptTemplate template = new PromptTemplate("Hello {name}, your age is {age}!", config);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "Alice"); // missing age
        assertEquals("Hello Alice, your age is !", template.format(vars));
    }

    @Test
    public void testMissingVariableThrowException() {
        Configuration config = new Configuration().set(PromptOptions.MISSING_VARIABLE_POLICY,
                MissingVariablePolicy.THROW_EXCEPTION);
        PromptTemplate template = new PromptTemplate("Hello {name}, your age is {age}.", config);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "Alice"); // missing age

        assertThrows(IllegalArgumentException.class, () -> template.format(vars));
    }

    @Test
    public void testNullVariableValue() {
        PromptTemplate template = new PromptTemplate("Hello {name}.");
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", null); // value is literally null
        assertEquals("Hello .", template.format(vars));
    }
}
