package com.players.core.domain;

import com.players.core.Logging.ConversationLogger;
import com.players.core.channel.MessageChannel;

/**
 * Responsibility: Models a single participant in the message-exchange protocol.
 *
 * OWNS:
 *   name        – readable identifier.
 *   inbound     – channel this player reads from.
 *   outbound    – channel this player writes to.
 *   sendCounter – monotonically increasing, appended to every outgoing payload.
 *                 Starts at 1.
 *   logger      – all console output delegated here; zero print statements in Player.
 *
 * DOES NOT KNOW:
 *   - Whether transport is in-process or multi-process.
 *   - How many round-trips occur (stop condition lives in the caller).
 *
 * TWO ROLES, ONE CLASS:
 *   run()                     → RESPONDER (runs on background thread)
 *   initiateConversation(...) → INITIATOR (runs on session/main thread)
 *
 * Role is a runtime decision made by the bootstrap class.
 */
public class Player implements Runnable, Identifiable{
    private int sendCounter = 1;

    private final String name;
    private final MessageChannel inbound;
    private final MessageChannel outbound;
    private final PlayerRole role;
    private final ConversationLogger logger;

    public Player(String name, MessageChannel inbound, MessageChannel outbound, PlayerRole role, ConversationLogger logger) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name shouldn't be null");

        if (inbound == null) throw new IllegalArgumentException("Inbound channel shouldn't be null");

        if (outbound == null) throw new IllegalArgumentException("Outbound channel shouldn't be null");

        if(logger == null) throw new IllegalArgumentException("Logger shouldn't be null");

        this.name = name;
        this.inbound = inbound;
        this.outbound = outbound;
        this.role = role;
        this.logger = logger;
    }

    public Player(String name, MessageChannel inbound, MessageChannel outbound, PlayerRole role) {
        this(name, inbound, outbound, role, new ConversationLogger());
    }

    // Responder Role
    @Override
    public void run() {
        if (role != PlayerRole.RESPONDER)
            throw new IllegalStateException(
                    name + " is " + role + " — only RESPONDER can call run()");

        logger.logReady(name, role);
        try {
            while(!Thread.currentThread().isInterrupted()) {
                Message received = inbound.receive();
                if (received.isStop()) break;

                logger.logReceived(name, received);

                Message reply = Message.payload(buildReply(received.getText()));
                outbound.send(reply);
                logger.logSent(name, reply);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.logShutdown(name);
    }

    //Initiator Role
    public void initiateConversation(String intialMessage, int maxRounds) throws InterruptedException{
        if (role != PlayerRole.INITIATOR)
            throw new IllegalStateException(
                    name + " is " + role + " — only INITIATOR can call initiateConversation()");

        logger.logReady(name, role);
        String textToSend = intialMessage;

        for (int round = 1; round <= maxRounds; round++) {
            Message outgoing = Message.payload(textToSend);
            outbound.send(outgoing);
            logger.logRound(name, round, "sent ", outgoing);

            Message incoming = inbound.receive();
            logger.logRound(name, round, "received ", incoming);

            textToSend = buildReply(incoming.getText());
        }

        // Graceful Reply to Responder that Coversation is Over
        outbound.send(Message.stop());
        logger.logConversationComplete(name, maxRounds);
    }

    private String buildReply(String receivedText) {
        return receivedText+""+sendCounter++;
    }

    @Override
    public String getName() {
        return name;
    }
}
