package com.players.inprocess;

import com.players.core.domain.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QueueChannel")
class QueueChannelTest {

    private final QueueChannel ch = new QueueChannel("test");

    @Test void sendAndReceive() throws Exception {
        ch.send(Message.payload("ping"));
        assertEquals("ping", ch.receive().getText());
    }

    @Test void fifoOrder() throws Exception {
        ch.send(Message.payload("A")); ch.send(Message.payload("B")); ch.send(Message.payload("C"));
        assertEquals("A", ch.receive().getText());
        assertEquals("B", ch.receive().getText());
        assertEquals("C", ch.receive().getText());
    }

    @Test @Timeout(5) void receiveBlocksUntilSend() throws Exception {
        AtomicReference<String> result = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try { result.set(ch.receive().getText()); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        t.setDaemon(true); t.start();
        Thread.sleep(60);
        assertNull(result.get());                    // still blocking
        ch.send(Message.payload("wake"));
        t.join(2_000);
        assertEquals("wake", result.get());
    }

    @Test @Timeout(5) void interruptUnblocksReceive() throws Exception {
        AtomicReference<Exception> caught = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try { ch.receive(); }
            catch (InterruptedException e) { caught.set(e); }
        });
        t.setDaemon(true); t.start();
        Thread.sleep(40);
        t.interrupt(); t.join(2_000);
        assertNotNull(caught.get());
    }

    @Test void terminatePassesThrough() throws Exception {
        ch.send(Message.stop());
        assertTrue(ch.receive().isStop());
    }

    @Test void blankNameThrows()  {
        assertThrows(IllegalArgumentException.class, () -> new QueueChannel(""));
    }
    @Test void nullNameThrows()   {
        assertThrows(IllegalArgumentException.class, () -> new QueueChannel(null));
    }
    @Test void closeIsNoOp()      {
        assertDoesNotThrow(() -> { ch.close(); ch.close(); });
    }
    @Test void getNameWorks()     {
        assertEquals("test", ch.getName());
    }
}