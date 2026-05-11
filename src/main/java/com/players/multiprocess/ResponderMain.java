package com.players.multiprocess;

import com.players.core.domain.Player;
import com.players.core.domain.TransportMode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ResponderMain {
    static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        System.out.println("  Transport :" + TransportMode.MULTI_PROCESS);
        System.out.println("=== Transport : " + TransportMode.MULTI_PROCESS);
        System.out.println("    Role      : RESPONDER (Player2)");
        System.out.println("    PID       : " + ProcessHandle.current().pid());
        System.out.println("    Port      : " + port);
        System.out.println();

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Player2] waiting for initiator on port "+ port);

            Socket clientSocket = serverSocket.accept();
            System.out.println("[Player2] connected: "+ clientSocket.getRemoteSocketAddress());

            TcpChannel channel = new TcpChannel("Player2-tcp", clientSocket);
            new Player("Player2", channel, channel).run();

            channel.close();

        } catch (IOException e) {
            System.err.println("[Player2] Fatal error: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("\n=== Responder finished | Transport: " + TransportMode.MULTI_PROCESS + " ===");
    }
}
