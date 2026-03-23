package org.venus.octopus.core.message.config;

import org.venus.octopus.common.config.ConfigOption;
import org.venus.octopus.common.config.ConfigOptions;

/**
 * Configuration options for Prompts and Messages.
 */
public class PromptOptions {

    public static final ConfigOption<MissingVariablePolicy> MISSING_VARIABLE_POLICY = ConfigOptions
            .key("prompt.missing.variable.policy").enumType(MissingVariablePolicy.class)
            .defaultValue(MissingVariablePolicy.KEEP_ORIGINAL).withDescription(
                    "Defines the behavior when a variable used in a PromptTemplate is missing from the variables map.");

}
