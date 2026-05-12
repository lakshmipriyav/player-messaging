package com.players.core.domain;

/**
 * Responsibility: Immutable value object representing one exchanged message.
 **/
public final class Message {
    private final MessageType type;
    private final String text;


    public Message(MessageType type, String text) {
        if (type == null) throw new IllegalArgumentException("MessageType shouldn't be null");
        if (text == null) throw new IllegalArgumentException("Message Text shouldn't be null");

        this.type = type;
        this.text = text;
    }

    public static Message payload(String text) {
        return new Message(MessageType.PAYLOAD, text);
    }

    public static Message stop() {
        return new Message(MessageType.STOP, "");
    }

    // Getters
    public MessageType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public boolean isStop() {
        return type == MessageType.STOP;
    }
    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", text='" + text + '\'' +
                '}';
    }
}
