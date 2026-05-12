package com.players.core.domain;

/**
 * Responsibility: Identifies the runtime role a Player is currently playing.
 *
 * INITIATOR – starts the conversation, drives the round-trip loop,
 *             owns the stop condition, and sends STOP when done.
 * RESPONDER – waits for messages, echoes each one back with a counter
 *             suffix, exits when STOP is received.
 *
 * Role is a runtime concern only — the same Player class acts in either
 * role depending on which method is invoked on it.
 */
public enum PlayerRole {
    INITIATOR,
    RESPONDER
}
