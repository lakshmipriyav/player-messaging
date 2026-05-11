package com.players.multiprocess;

import com.players.core.channel.AbstractChannel;
import com.players.core.domain.Message;
import com.players.core.domain.MessageType;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TcpChannel extends AbstractChannel {

    private static final String DELIMETER = "|";

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public TcpChannel(String name, Socket socket) throws IOException {
        super(name);
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
    }

    @Override
    public void send(Message message) throws InterruptedException {
        writer.println(message.getType().name() + DELIMETER + message.getText());
        if (writer.checkError()){
            throw new RuntimeException("TCP Channel ["+getName()+"]: write error");
        }
    }

    @Override
    public Message receive() throws InterruptedException {
        try {
            String line = reader.readLine();
            if (line == null) {
                Thread.currentThread().interrupt();
                throw new InterruptedException("TCP Channel ["+getName()+"]: connection closed by remote");
            }

            Message message = deserialise(line);

            if(message.isStop()) {
                Thread.currentThread().interrupt();
                throw new InterruptedException("TCP Channel ["+getName()+"]: Stop received");
            }
            return message;
        } catch (IOException e) {
            throw new InterruptedException("TCP Channel ["+getName()+"] IO error: "+ e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            writer.println(MessageType.STOP.name() + DELIMETER);
            socket.close();
        } catch (IOException e){
            //ignore
        }
    }

    private Message deserialise(String line) {
        int seperator = line.indexOf(DELIMETER);
        if (seperator < 0) return Message.payload(line);

        String typePart = line.substring(0, seperator);
        String textPart = line.substring(seperator+1);

        try {
            MessageType type = MessageType.valueOf(typePart);
            return (type == MessageType.STOP) ? Message.stop() : Message.payload(textPart);
        } catch (IllegalArgumentException e){
            return Message.payload(textPart);
        }
    }
}
