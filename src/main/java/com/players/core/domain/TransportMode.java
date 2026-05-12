package com.players.core.domain;
/**
 * Responsibility: Identifies the active transport for a given deployment.
 *
 * IN_PROCESS    – same JVM, two threads, LinkedBlockingQueue transport.
 * MULTI_PROCESS – separate JVMs, TCP socket transport (Mode 2).
 */
public enum TransportMode {
    IN_PROCESS,
    MULTI_PROCESS
}
