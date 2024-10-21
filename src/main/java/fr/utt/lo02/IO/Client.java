package fr.utt.lo02.IO;

import java.net.*;

public class Client {
    private String ip;
    private int port;
    private Socket server;
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    public void start() {
        try {
            System.out.println("Connecting to " + ip + " on port " + port);
            server = new Socket(ip, port);
            System.out.println("Connected to " + server.getRemoteSocketAddress());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
