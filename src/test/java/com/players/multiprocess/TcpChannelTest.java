package com.players.multiprocess;

import com.players.core.domain.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TcpChannel")
class TcpChannelTest {

    record Pair(TcpChannel server, TcpChannel client,
                ServerSocket ss) implements AutoCloseable {
        public void close() {
            server.close(); client.close();
            try { ss.close(); } catch (Exception ignored) {}
        }
    }

    private static Pair open() throws Exception {
        var ss = new ServerSocket(0);
        var ref = new AtomicReference<TcpChannel>();
        var latch = new CountDownLatch(1);
        Thread t = new Thread(() -> {
            try { ref.set(new TcpChannel("client",
                    new Socket("localhost", ss.getLocalPort())));
                latch.countDown();
            } catch (Exception e) { throw new RuntimeException(e); }
        });
        t.setDaemon(true); t.start();
        var server = new TcpChannel("server", ss.accept());
        latch.await();
        return new Pair(server, ref.get(), ss);
    }

    @Test
    void sendAndReceive() throws Exception {
        try (var p = open()) {
            p.client().send(Message.payload("hello"));
            assertEquals("hello", p.server().receive().getText());
        }
    }

    @Test void fifoOrder() throws Exception {
        try (var p = open()) {
            p.client().send(Message.payload("1"));
            p.client().send(Message.payload("2"));
            p.client().send(Message.payload("3"));
            assertEquals("1", p.server().receive().getText());
            assertEquals("2", p.server().receive().getText());
            assertEquals("3", p.server().receive().getText());
        }
    }

    @Test void bidirectional() throws Exception {
        try (var p = open()) {
            p.client().send(Message.payload("ping"));
            assertEquals("ping", p.server().receive().getText());
            p.server().send(Message.payload("pong"));
            assertEquals("pong", p.client().receive().getText());
        }
    }

    @Test @Timeout(5) void closeSignalsTerminate() throws Exception {
        try (var p = open()) {
            p.client().close();
            assertThrows(InterruptedException.class, () -> p.server().receive());
        }
    }

    @Test void longTextSurvivesWire() throws Exception {
        String big = "Hello" + "1".repeat(200);
        try (var p = open()) {
            p.client().send(Message.payload(big));
            assertEquals(big, p.server().receive().getText());
        }
    }

    @Test void closeIsIdempotent() throws Exception {
        try (var p = open()) {
            assertDoesNotThrow(() -> { p.client().close(); p.client().close(); });
        }
    }

    @Test void getNameWorks() throws Exception {
        try (var p = open()) {
            assertEquals("server", p.server().getName());
            assertEquals("client", p.client().getName());
        }
    }
}