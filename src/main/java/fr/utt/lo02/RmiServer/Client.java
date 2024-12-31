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

/**
 * The Client class represents a client in the RMI architecture.
 * It handles communication with the server and manages the game state for the client.
 */
public class Client extends UnicastRemoteObject implements ClientRemote, Runnable {
    private ServerRemote serverRemote;
    private Integer playerId = null;
    private Game game;
    private GUIIOHandler io;
    private GUIManager guiManager;
    private Thread checkServerConnectionThread;

    /**
     * Constructs a new Client instance and initializes the GUI manager and IO handler.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    public Client() throws RemoteException {
        super();
        this.guiManager = new GUIManager();
        this.io = this.guiManager.getGUI();
        this.connectToServer();
    }

    /**
     * Connects the client to the server.
     */
    private void connectToServer() {
        try {
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
            this.checkServerConnectionThread = new Thread(this);
            this.checkServerConnectionThread.start();
        } catch (Exception e) {
            System.out.println("Error while connecting to the server");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Runs the thread to check the server connection.
     */
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

    /**
     * Gets the username from the IO handler.
     *
     * @return the username
     * @throws RemoteException if a remote communication error occurs
     */
    public String getUserName() throws RemoteException {
        String username = this.io.getUserName();
        return username;
    }

    /**
     * Sets the game instance from a JSON string.
     *
     * @param json the JSON string representing the game instance
     * @throws RemoteException if a remote communication error occurs
     */
    public void setGameInstance(String json) throws RemoteException {
        System.out.println(json);
        this.game = Game.getInstance(json);
        this.guiManager.setGameInstance(this.game, this);
        System.out.println("Game instance set");
        System.out.println(GameDataConverter.toJson(game));
    }

    /**
     * Sets the player ID for the client.
     *
     * @param playerId the ID of the player
     * @throws RemoteException if a remote communication error occurs
     */
    public void setPlayerId(int playerId) throws RemoteException {
        this.playerId = playerId;
        this.guiManager.setPlayerId(playerId);
        System.out.println("Player id set : " + playerId);
    }

    /**
     * Receives a message from a player and updates the chat GUI.
     *
     * @param playerId the ID of the player sending the message
     * @param message the message content
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void receiveMessage(int playerId, String message) throws RemoteException {
        this.guiManager.getChatGUI().receiveMessage(playerId, message);
    }

    /**
     * Displays an error message to the player.
     *
     * @param message the error message
     * @param playerId the ID of the player
     * @throws RemoteException if a remote communication error occurs
     */
    public void displayError(String message, int playerId) throws RemoteException {
        this.io.displayError(message, playerId);
    }

    /**
     * Gets the starting cell ID for the player.
     *
     * @param playerId the ID of the player
     * @return the starting cell ID
     * @throws RemoteException if a remote communication error occurs
     */
    public int getStartingCellId(int playerId) throws RemoteException {
        this.checkId(playerId);
        return this.io.getStartingCellId(playerId);
    }

    /**
     * Gets the command order from the player.
     *
     * @param playerId the ID of the player
     * @return an array of Command objects representing the chosen command orders
     * @throws RemoteException if a remote communication error occurs
     */
    public Command[] getCommandOrder(int playerId) throws RemoteException {
        this.checkId(playerId);
        return this.io.getCommandOrders(playerId);
    }

    /**
     * Expands the player's ships to new cells.
     *
     * @param playerId the ID of the player
     * @param nShips the number of ships to expand
     * @return a 2D array where each sub-array contains the cell ID and the number of ships placed on that cell
     * @throws RemoteException if a remote communication error occurs
     */
    public int[][] expand(int playerId, int nShips) throws RemoteException {
        this.checkId(playerId);
        return this.io.expand(playerId, nShips);
    }

    /**
     * Explores new cells with the player's fleet.
     *
     * @param playerId the ID of the player
     * @param nFleet the number of fleets to explore with
     * @return a 3D array where each sub-array contains the details of the exploration
     * @throws RemoteException if a remote communication error occurs
     */
    public int[][][] explore(int playerId, int nFleet) throws RemoteException {
        this.checkId(playerId);
        return this.io.explore(playerId, nFleet);
    }

    /**
     * Exterminates enemy ships on specific cells.
     *
     * @param playerId the ID of the player
     * @param nFleet the number of fleets to exterminate
     * @return a 2D array where each sub-array contains the details of the extermination
     * @throws RemoteException if a remote communication error occurs
     */
    public int[][] exterminate(int playerId, int nFleet) throws RemoteException {
        this.checkId(playerId);
        return this.io.exterminate(playerId, nFleet);
    }

    /**
     * Scores a sector for a specific player.
     *
     * @param id the ID of the player
     * @return the ID of the scored sector
     */
    public int score(int id) {
        this.checkId(id);
        return this.io.score(id);
    }

    /**
     * Displays the winners of the game.
     *
     * @param winnersIds an array of player IDs representing the winners
     * @throws RemoteException if a remote communication error occurs
     */
    public void displayWinner(int[] winnersIds) throws RemoteException {
        this.io.displayWinner(winnersIds);
    }

    /**
     * Displays a draw message.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    public void displayDraw() throws RemoteException {
        this.io.displayDraw();
    }

    /**
     * Disconnects the client.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void deconnect() throws RemoteException {
        System.exit(0);
    }

    /**
     * Checks if the player ID is valid.
     *
     * @param id the ID of the player
     */
    private void checkId(int id) {
        if (this.playerId == null) {
            throw new IllegalGameStateExeceptions("Player id not set");
        }
        if (this.playerId != id) {
            throw new IllegalGameStateExeceptions("Wrong player id");
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param playerId the ID of the player sending the message
     * @param msg the message content
     */
    public void sendMessage(int playerId, String msg) {
        try {
            this.serverRemote.sendMessages(playerId, msg);
        } catch (RemoteException _) {
            // Handle exception
        }
    }
}