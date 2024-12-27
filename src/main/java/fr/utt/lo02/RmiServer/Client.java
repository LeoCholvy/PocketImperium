package fr.utt.lo02.RmiServer;

import fr.utt.lo02.IO.CLI;
import fr.utt.lo02.app.GUIManager;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.IllegalGameStateExeceptions;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.data.GameDataConverter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientRemote, Runnable {
    private ServerRemote serverRemote;
    private Integer playerId = null;
    private Game game;
    private GUIIOHandler io;
    private GUIManager guiManager;
    private Thread checkServerConnectionThread;

    public Client() throws RemoteException {
        super();
        this.guiManager = new GUIManager();
        this.io = this.guiManager.getGUI();
        this.connectToServer();
    }

    private void connectToServer() {
        try {
            // String ip = "192.168.1.82";
            // String ip = "localhost";
            String ip;
            while(true) {
                try {
                    ip = this.guiManager.getGUI().getIp();
                    break;
                } catch (Exception e) {
                    System.out.println("Host not found");
                }
            }

            this.serverRemote = (ServerRemote) Naming.lookup("rmi://"+ip+":1099/PocketImperium");
            System.out.println("Connected to the server");
            if (this.serverRemote.registerClient(this)) {
                System.out.println("Can't register to the server");
                System.exit(1);
            }
            this.guiManager.getGUI().displayGame();
            this.checkServerConnectionThread = new Thread(this);
            this.checkServerConnectionThread.start();
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Error while connecting to the server");
            e.printStackTrace();
            System.exit(1);
            // connectToServer();
        }
    }
    public void run() {
        while (true) {
            try {
                this.serverRemote.ping();
                System.out.println("Ping: Server is reachable");
                Thread.sleep(30000);
            } catch (Exception e) {
                System.out.println("The server is not reachable");
                System.exit(1);
            }
        }
    }

    // public void setCurrentAction(String action) throws RemoteException {
    //
    // }

    public String getUserName() throws RemoteException {
        String username = this.io.getUserName();
        return username;
    }

    public void setGameInstance(String json) throws RemoteException {
        this.game = Game.getInstance(json);
        // update gui
        // this.io = new CLI(this.game);
        this.guiManager.setGameInstance(this.game, this);

        System.out.println("Game instance set");
        System.out.println(GameDataConverter.toJson(game));
    }

    public void setPlayerId(int playerId) throws RemoteException {
        this.playerId = playerId;
        this.guiManager.setPlayerId(playerId);
        System.out.println("Player id set : " + playerId);
    }

    @Override
    public void receiveMessage(int playerId, String message) throws RemoteException {
        this.guiManager.getChatGUI().receiveMessage(playerId, message);
    }

    public void displayError(String message, int playerId) throws RemoteException {
        this.io.displayError(message, playerId);
    }

    public int getStartingCellId(int playerId) throws RemoteException {
        this.checkId(playerId);
        return this.io.getStartingCellId(playerId);
    }

    public Command[] getCommandOrder(int playerId) throws RemoteException {
        this.checkId(playerId);
        // Player player = this.game.getPlayer(playerId);
        // System.out.println("Player " + player.getName() + ", enter your command");
        // System.out.println("1. Expand");
        // System.out.println("2. Explore");
        // System.out.println("3. Exterminate");
        // System.out.println("input example : 213 for Explore, Expand, Exterminate");
        // BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // try {
        //     System.out.print(">>>");
        //     String input = reader.readLine();
        //     Command[] commands = new Command[3];
        //     if (input.length() != 3 || !input.contains("1") || !input.contains("2") || !input.contains("3")) {
        //         displayError("Invalid input, please enter a valid command", playerId);
        //         return this.getCommandOrder(playerId);
        //     }
        //     for (int i = 0; i < 3; i++) {
        //         if (input.charAt(i) == '1') {
        //             commands[i] = Command.EXPAND;
        //         } else if (input.charAt(i) == '2') {
        //             commands[i] = Command.EXPLORE;
        //         } else if (input.charAt(i) == '3') {
        //             commands[i] = Command.EXTERMINATE;
        //         }
        //     }
        //     return commands;
        // } catch (Exception e) {
        //     displayError("Invalid input, please enter a valid command", playerId);
        //     return this.getCommandOrder(playerId);
        // }
        return this.io.getCommandOrders(playerId);
    }

    public int[][] expand(int playerId, int nShips) throws RemoteException {
        this.checkId(playerId);
        return this.io.expand(playerId, nShips);
    }

    public int[][][] explore(int playerId, int nFleet) throws RemoteException {
        this.checkId(playerId);
        return this.io.explore(playerId, nFleet);
    }

    public int[][] exterminate(int playerId, int nFleet) throws RemoteException {
        this.checkId(playerId);
        return this.io.exterminate(playerId, nFleet);
    }

    public int score(int id) {
        this.checkId(id);
        return this.io.score(id);
    }

    public void displayWinner(int[] winnersIds) throws RemoteException {
        this.io.displayWinner(winnersIds);
    }

    public void displayDraw() throws RemoteException {
        this.io.displayDraw();
    }

    private void checkId(int id) {
        if (this.playerId == null) {
            throw new IllegalGameStateExeceptions("Player id not set");
        }
        if (this.playerId != id) {
            throw new IllegalGameStateExeceptions("Wrong player id");
        }
    }

    public void sendMessage(int playerId, String msg) {
        try {
            this.serverRemote.sendMessages(playerId, msg);
        } catch (RemoteException _) {

        }
    }
}
