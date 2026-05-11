package com.players.inprocess;

import com.players.core.conversation.RoundTripConversation;
import com.players.core.domain.Player;
import com.players.core.domain.PlayerRole;
import com.players.core.domain.TransportMode;

public class InProcessMain {
    private static final int ROUNDS = 10;
    private static final String INTITAL_MESSAGE = "Hello";

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Transport : " + TransportMode.IN_PROCESS);
        System.out.println("    Threads   : 2  (main + responder-thread)");
        System.out.println("    Rounds    : " + ROUNDS);
        System.out.println("    PID       : " + ProcessHandle.current().pid());
        System.out.println();

        //Two Uni-directional Channels
        QueueChannel channelAtoB = new QueueChannel("A->B");
        QueueChannel channelBtoA = new QueueChannel("B->A");

        // Player1: outbound = A->B,  inbound = B->A  (initiator)
        Player player1 = new Player("Player1", channelBtoA, channelAtoB, PlayerRole.INITIATOR);

        // Player2: outbound = B->A,  inbound = A->B  (responder)
        Player player2 = new Player("Player2", channelAtoB, channelBtoA, PlayerRole.RESPONDER);

        RoundTripConversation session = new RoundTripConversation(player1, player2, INTITAL_MESSAGE, ROUNDS);
        session.start();

        System.out.println();
        System.out.println("=== Session finished | isFinished=" + session.isFinish()
                + " | Transport: " + TransportMode.IN_PROCESS + " ===");
    }
}
