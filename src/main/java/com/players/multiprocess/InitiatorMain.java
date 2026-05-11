package com.players.multiprocess;

import com.players.core.domain.Player;
import com.players.core.domain.TransportMode;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class InitiatorMain {
    private static final int DEFAULT_PORT = 9090;
    private static final String DEFAULT_HOST = "localhost";
    private static final int ROUNDS = 10;
    private static final String INITIAL_MESSAGE = "Hello";

    private static final int MAX_CONNECTION_RETRY = 10;
    private static final long RETRY_DELAY_MS = 500L;

    public static void main (String[] args) throws InterruptedException {
        String host = (args.length > 0) ? args[0] : DEFAULT_HOST;
        int port = (args.length > 1) ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        System.out.println("=== Transport : " + TransportMode.MULTI_PROCESS);
        System.out.println("    Role      : INITIATOR (Player1)");
        System.out.println("    PID       : " + ProcessHandle.current().pid());
        System.out.println("    Target    : " + host + ":" + port);
        System.out.println();

        Socket socket = connectWithRetry(host, port);

        try{
            TcpChannel channel = new TcpChannel("Player1-tcp", socket);
            new Player("Player1", channel, channel).initiateConversation(INITIAL_MESSAGE, ROUNDS);

            channel.close();

        } catch (IOException e) {
            System.err.println("[Player1] Fatal error: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("\n=== Initiator finished | Transport: "
                + TransportMode.MULTI_PROCESS + " ===");
    }

    private static Socket connectWithRetry(String host, int port) throws InterruptedException {
        for(int attempt = 1; attempt <= MAX_CONNECTION_RETRY; attempt++) {
            try {
                Socket socket = new Socket(host, port);
                System.out.println("[Player1] connected to " + host + ":" + port + "\n");
                return socket;
            } catch (IOException e) {
                System.out.printf("[Player1] attempt %d/%d failed — retrying in %dms...%n",
                        attempt, MAX_CONNECTION_RETRY, RETRY_DELAY_MS);
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw new RuntimeException("[Player1] Could not connect to " + host + ":" + port
                + " after " + MAX_CONNECTION_RETRY + " attempts");
    }
}
