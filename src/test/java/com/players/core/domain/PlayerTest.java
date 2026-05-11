package com.players.core.domain;

import com.players.inprocess.QueueChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Player")
class PlayerTest {

    private static Thread responder(QueueChannel in, QueueChannel out) {
        return daemon(new Player("R", in, out, PlayerRole.RESPONDER));
    }

    private static Thread daemon(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
        return t;
    }

    @Test
    void firstReply() throws Exception {
        var in = new QueueChannel("in"); var out = new QueueChannel("out");
        Thread t = responder(in, out);
        in.send(Message.payload("Hello"));
        assertEquals("Hello1", out.receive().getText());
        t.interrupt(); t.join(1_000);
    }

    @Test
    void counterIncrements() throws Exception {
        var in = new QueueChannel("in"); var out = new QueueChannel("out");
        Thread t = responder(in, out);
        in.send(Message.payload("A"));
        in.send(Message.payload("B"));
        in.send(Message.payload("C"));
        assertEquals(
                List.of("A1", "B2", "C3"),
                List.of(out.receive().getText(),
                        out.receive().getText(),
                        out.receive().getText())
        );
        t.interrupt(); t.join(1_000);
    }

    @Test @Timeout(5)
    void stopsOnTerminate() throws Exception {
        var in = new QueueChannel("in"); var out = new QueueChannel("out");
        Thread t = responder(in, out);
        in.send(Message.stop());
        t.join(2_000);
        assertFalse(t.isAlive());
    }

    @Test @Timeout(10)
    void textAccumulates() throws Exception {
        var in = new QueueChannel("in"); var out = new QueueChannel("out");
        Thread t = responder(in, out);
        in.send(Message.payload("Hello"));
        assertEquals("Hello1",  out.receive().getText());
        in.send(Message.payload("Hello1"));
        assertEquals("Hello12", out.receive().getText());
        t.interrupt(); t.join(1_000);
    }

    @Test @Timeout(10)
    void fullConversation() throws Exception {
        var ab = new QueueChannel("A->B"); var ba = new QueueChannel("B->A");
        Thread t = daemon(new Player("P2", ab, ba, PlayerRole.RESPONDER));
        new Player("P1", ba, ab, PlayerRole.INITIATOR)
                .initiateConversation("Hello", 10);
        t.join(3_000);
        assertFalse(t.isAlive());
    }

    @Test
    void runOnInitiatorThrows() {
        var p = new Player("P1", new QueueChannel("in"), new QueueChannel("out"),PlayerRole.INITIATOR);
        assertThrows(IllegalStateException.class, p::run);
    }

    @Test
    void initiateOnResponderThrows() {
        var p = new Player("P2", new QueueChannel("in"), new QueueChannel("out"), PlayerRole.RESPONDER);
        assertThrows(IllegalStateException.class,
                () -> p.initiateConversation("Hello", 1));
    }
}