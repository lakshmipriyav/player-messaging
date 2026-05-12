package com.players.core.domain;

/**
 * Responsibility: Classifies every message so player and channel logic can
 * distinguish normal payloads from control signals without parsing text.
 * <p>
 * PAYLOAD   – carries user-visible text with a counter.
 * STOP – control signal that orders the receiver to stop its loop.
 */
public enum MessageType {
    PAYLOAD,
    STOP
}
