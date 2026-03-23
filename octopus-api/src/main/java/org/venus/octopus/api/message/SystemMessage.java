package org.venus.octopus.api.message;

/**
 * 系统提示消息
 */
public class SystemMessage extends BaseMessage {

    public SystemMessage(String content) {
        super(content);
    }

    @Override
    public MessageType getType() {
        return MessageType.SYSTEM;
    }
}
