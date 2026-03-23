package org.venus.octopus.api.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.octopus.common.exception.OctopusException;

import java.util.Map;
import java.util.ServiceLoader;

public final class EmbeddingModels {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingModels.class);

    private EmbeddingModels() {
    }

    public static EmbeddingModel create(String providerName, Map<String, Object> properties) {
        if (providerName == null || providerName.isBlank()) {
            throw new IllegalArgumentException("providerName cannot be empty");
        }

        ServiceLoader<EmbeddingModelFactory> loader = ServiceLoader.load(EmbeddingModelFactory.class);
        for (EmbeddingModelFactory factory : loader) {
            log.debug("Found EmbeddingModelFactory: [{}]", factory.providerName());
            if (providerName.equalsIgnoreCase(factory.providerName())) {
                return factory.createModel(properties);
            }
        }

        throw new OctopusException("SPI Loading Failed: 无法找到名为 '" + providerName + "' 的 EmbeddingModelFactory");
    }
}
