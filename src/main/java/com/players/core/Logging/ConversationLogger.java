package com.players.core.Logging;

import com.players.core.domain.Message;
import com.players.core.domain.PlayerRole;

/**
 * Responsibility: Centralises ALL console output produced during a conversation.
 **/
public class ConversationLogger {

    /**player is ready and waiting in the given role.*/
    public void logReady(String playerName, PlayerRole role) {
        System.out.printf("[%s][%s] ready.%n", playerName, role);
    }

    /** Logs a message received by a player (responder side). */
    public void logReceived(String playerName, Message message) {
        System.out.printf("[%s] received : \"%s\"%n", playerName, message.getText());
    }

    /** Logs a message sent by a player (responder side). */
    public void logSent(String playerName, Message message) {
        System.out.printf("[%s] sent : \"%s\"%n", playerName, message.getText());
    }

    /** Logs an initiator round-trip event (send or receive). */
    public void logRound(String playerName, int round, String direction, Message message) {
        System.out.printf("[%s] %s (round %2d): \"%s\"%n", playerName, direction, round, message.getText());
        System.out.println();
    }

    /** Logs that a player's loop has ended & it is shutting down. */
    public void logShutdown(String playerName) {
        System.out.printf("[%s] shutting down.%n", playerName);
    }

    /** Logs that the initiator has completed all configured round-trips. */
    public void logConversationComplete(String playerName, int rounds) {
        System.out.printf("[%s] completed %d round-trips. Conversation finished.%n",
                playerName, rounds);
    }
}
