package org.venus.octopus.api.message;

import java.util.Map;

/**
 * Top-level interface for messages.
 * <p>
 * Represents a single message exchanged between the Agent and users, tools, or
 * AI models.
 * </p>
 */
public interface Message {

    MessageType getType();

    String getContent();

    Map<String, Object> getMetadata();
}
