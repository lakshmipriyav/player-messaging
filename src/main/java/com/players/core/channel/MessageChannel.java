package com.players.core.channel;

import com.players.core.domain.Identifiable;
import com.players.core.domain.Message;
/**
 * Responsibility: Abstraction over the transport used to exchange Messages.
 *   - All operations are SYNCHRONOUS and BLOCKING.
 *   - send()  blocks until the transport accepts the message.
 *   - receive() blocks until a message is available.
 *   - close() must be idempotent and must never throw.
 */
public interface MessageChannel extends Identifiable {

    void send(Message message) throws InterruptedException;

    Message receive() throws InterruptedException;

    void close();
}
