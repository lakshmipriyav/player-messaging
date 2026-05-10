package com.players.core.conversation;

public interface Conversation {
    void start() throws InterruptedException;

    boolean isFinish();
}
