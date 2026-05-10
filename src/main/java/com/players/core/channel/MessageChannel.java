package com.players.core.channel;

import com.players.core.domain.Identifiable;
import com.players.core.domain.Message;

public interface MessageChannel extends Identifiable {

    void send(Message message) throws InterruptedException;

    Message receive() throws InterruptedException;

    void close();
}
