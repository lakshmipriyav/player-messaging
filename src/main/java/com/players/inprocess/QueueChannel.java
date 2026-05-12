package com.players.inprocess;

import com.players.core.channel.AbstractChannel;
import com.players.core.domain.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * Responsibility: In-process MessageChannel that passes Messages between two
 * threads via a LinkedBlockingQueue.
 */
public class QueueChannel extends AbstractChannel {

    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public QueueChannel(String name) {
        super(name);
    }

    @Override
    public void send(Message message) throws InterruptedException{
        queue.put(message);
    }

    @Override
    public Message receive() throws InterruptedException{
        return queue.take();
    }

    @Override
    public void close() {
        //Intentionally Empty.
    }
}
