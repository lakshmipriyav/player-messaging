package com.players.core.domain;

import com.players.core.Logging.ConversationLogger;
import com.players.core.channel.MessageChannel;

public class Player implements Runnable, Identifiable{
    private int sendCounter = 1;

    private final String name;
    private final MessageChannel inbound;
    private final MessageChannel outbound;
    private final ConversationLogger logger;

    public Player(String name, MessageChannel inbound, MessageChannel outbound, ConversationLogger logger) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name shouldn't be null");

        if (inbound == null) throw new IllegalArgumentException("Inbound channel shouldn't be null");

        if (outbound == null) throw new IllegalArgumentException("Outbound channel shouldn't be null");

        if(logger == null) throw new IllegalArgumentException("Logger shouldn't be null");

        this.name = name;
        this.inbound = inbound;
        this.outbound = outbound;
        this.logger = logger;
    }

    public Player(String name, MessageChannel inbound, MessageChannel outbound) {
        this(name, inbound, outbound, new ConversationLogger());
    }

    // Responder Role
    @Override
    public void run() {
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
        logger.logReady(name, PlayerRole.INITIATOR);
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
