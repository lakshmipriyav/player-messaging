package com.players.core.conversation;

import com.players.core.domain.Player;

//Used by InProcessMain
public class RoundTripConversation implements Conversation{
    private static final long JOIN_TIMEOUT_MS      = 3_000L;
    private static final long INTERRUPT_TIMEOUT_MS = 1_000L;

    private final Player initiator;
    private final Player responder;
    private final String initialMessage;
    private final int maxRounds;

    private volatile boolean finished = false;

    public RoundTripConversation(Player initiator, Player responder, String initialMessage, int maxRounds) {
        if (initiator == null) throw new IllegalArgumentException("Initiator shouldn't be null");
        if (responder == null) throw new IllegalArgumentException("Responder shouldn't be null");
        if (initialMessage == null) throw new IllegalArgumentException("Initial Message shouldn't be null");
        if (maxRounds <= 0) throw new IllegalArgumentException("Max Round should be atleast 1");

        this.initiator = initiator;
        this.responder = responder;
        this.initialMessage = initialMessage;
        this.maxRounds = maxRounds;
    }

    @Override
    public void start() throws InterruptedException {
        Thread responderThread = new Thread(responder, responder.getName()+"-thread");
        responderThread.setDaemon(true);
        responderThread.start();

        initiator.initiateConversation(initialMessage, maxRounds);

        responderThread.join(JOIN_TIMEOUT_MS);
        if (responderThread.isAlive()){
            responderThread.interrupt();
            responderThread.join(INTERRUPT_TIMEOUT_MS);
        }
        finished = true;

    }

    @Override
    public boolean isFinish() {

        return finished;
    }
}
