package org.venus.octopus.api.message;

/**
 * 用户消息
 */
public class HumanMessage extends BaseMessage {

    public HumanMessage(String content) {
        super(content);
    }

    @Override
    public MessageType getType() {
        return MessageType.HUMAN;
    }
}
