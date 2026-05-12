package com.players.core.conversation;
/**
 * Responsibility: Represents a bounded, self-contained conversation session.
 *
 * Player has no knowledge of how many rounds happen or how threads are managed.
 */
public interface Conversation {
    void start() throws InterruptedException;

    boolean isFinish();
}
