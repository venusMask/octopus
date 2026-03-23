package org.venus.octopus.api.memory;

import org.venus.octopus.api.message.Message;

import java.util.List;

/**
 * Chat session context memory interface.
 * <p>
 * Provides long-term or short-term multi-turn conversation context management
 * capabilities, using sessionId as a unique identifier for users. It can be
 * used for normal LLM continuous conversations or as an important knowledge
 * supplement layer during Agent execution.
 * </p>
 */
public interface ChatMemory {

    /**
     * Appends multiple messages to a specified session.
     *
     * @param sessionId
     *            Unique identifier for the session
     * @param messages
     *            List of messages to append
     */
    void add(String sessionId, List<Message> messages);

    /**
     * Appends a single message to a specified session.
     *
     * @param sessionId
     *            Unique identifier for the session
     * @param message
     *            A single new message
     */
    void add(String sessionId, Message message);

    /**
     * Gets all historical messages for a specified session (controlled by
     * truncation/eviction policies internal to the specific implementation).
     *
     * @param sessionId
     *            Session identifier
     * @return An immutable list or a safe copy of historical messages, or an empty
     *         list if none exist
     */
    List<Message> get(String sessionId);

    /**
     * Clears all content for a specified session.
     *
     * @param sessionId
     *            Session identifier
     */
    void clear(String sessionId);
}
