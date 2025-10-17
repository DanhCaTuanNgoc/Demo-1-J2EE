package com.example.demo.model;

/**
 * Simple Message class for conversation history
 * Since we're using REST API only, we don't need Spring AI dependencies
 */
public class Message {
    private final String content;
    private final MessageType type;

    public Message(String content, MessageType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", type=" + type +
                '}';
    }

    public enum MessageType {
        USER, ASSISTANT
    }
}
