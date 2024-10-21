package fr.utt.lo02.IO;

import java.net.*;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private Socket[] clients = new Socket[3];
    private int nPlayers;
    public Server(int port, int nPlayers) {
        this.port = port;
        this.nPlayers = nPlayers;
    }
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            for (int i = 0; i < nPlayers; i++) {
                clients[i] = serverSocket.accept();
                System.out.println("Player " + (i + 1) + " connected.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

