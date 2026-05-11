package com.players.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("PlayerRole Enum")
class PlayerRoleTest {
    @Test void initiatorExists() {
        assertEquals(PlayerRole.INITIATOR, PlayerRole.valueOf("INITIATOR"));
    }

    @Test void responderExists() {
        assertEquals(PlayerRole.RESPONDER, PlayerRole.valueOf("RESPONDER"));
    }

    @Test void exactlyTwoRoles() {
        assertEquals(2, PlayerRole.values().length);
    }
}