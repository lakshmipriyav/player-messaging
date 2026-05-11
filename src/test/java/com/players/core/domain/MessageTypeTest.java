package com.players.core.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MessageType Enum")
class MessageTypeTest {
    @Test
    void payloadExists() {
        assertEquals(MessageType.PAYLOAD, MessageType.valueOf("PAYLOAD"));
    }

    @Test void terminateExists() {
        assertEquals(MessageType.STOP, MessageType.valueOf("STOP"));
    }

    @Test void exactlyTwoValues() {
        assertEquals(2, MessageType.values().length);
    }

    @Test void unknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> MessageType.valueOf("A"));
    }
}