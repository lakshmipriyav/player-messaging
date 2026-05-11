package com.players.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Message Value Object")
class MessageTest {
    @Test void payloadFactory()  {
        Message m = Message.payload("Hi");
        assertEquals(MessageType.PAYLOAD, m.getType());
        assertEquals("Hi", m.getText());
        assertFalse(m.isStop());
    }
    @Test void stopFactory() {
        Message m = Message.stop();
        assertEquals(MessageType.STOP, m.getType());
        assertEquals("", m.getText());
        assertTrue(m.isStop());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "Hello", "Hello1"})
    void acceptsAnyNonNull(String t) {
        assertEquals(t, Message.payload(t).getText());
    }

    @Test void nullRejected() {
        assertThrows(IllegalArgumentException.class, () -> Message.payload(null));
    }
}