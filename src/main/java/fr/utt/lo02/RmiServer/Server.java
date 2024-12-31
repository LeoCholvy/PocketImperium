package fr.utt.lo02.RmiServer;

import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.data.DataManipulator;
import fr.utt.lo02.data.GameDataConverter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static java.lang.System.exit;

/**
 * The Server class represents the server in the RMI architecture.
 * It handles client registration, game state management, and communication with clients.
 */
public class Server extends UnicastRemoteObject implements ServerRemote, IOHandler {
    private String state = null;
    private Game game;
    private HashMap<Player, ClientRemote> clientsRemote;
    private boolean start;
    private final static Object lock = new Object();

    /**
     * Constructs a new Server instance and starts the server.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    public Server() throws RemoteException {
        super();
        this.startServer();
    }

    /**
     * Starts the RMI server and binds it to the registry.
     */
    private void startServer() {
        try {
            LocateRegistry.createRegistry(1099);

            String url = "rmi://localhost:1099/PocketImperium";
            java.rmi.Naming.rebind(url, this);
            System.out.println("Server started at " + url);
            // display the server IP address
            java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
            System.out.println("Server IP Address: " + addr.getHostAddress());
        } catch (Exception e) {
            throw new GameRemoteExeceptions("Error while creating the server");
        }
    }

    /**
     * Pings the server to check if it is reachable.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    public void ping() throws RemoteException {
        // Do nothing
    }

    /**
     * Registers a client with the server.
     *
     * @param client the client to register
     * @return true if the client could not be registered, false otherwise
     * @throws RemoteException if a remote communication error occurs
     */
    public boolean registerClient(ClientRemote client) throws RemoteException {
        String name = client.getUserName();
        if (this.state != null && this.state.equals("waitingForPlayers")) {
            for (Player p : this.clientsRemote.keySet()) {
                if (p.getName().equals(name)) {
                    if (this.clientsRemote.get(p) != null) {
                        return true;
                    }
                    client.setPlayerId(p.getId());
                    client.setGameInstance(GameDataConverter.toJson(this.game));
                    System.out.println("Player " + p.getName() + " connected, playerId = " + p.getId());
                    this.clientsRemote.put(p, client);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    return false;
                }
            }
            return true;
        } else if (this.state != null && this.state.equals("waitingNewPlayer")) {
            Player p = new Player(name, this.clientsRemote.size());
            this.clientsRemote.put(p, client);
            client.setGameInstance(DataManipulator.loadSave("model"));
            client.setPlayerId(p.getId());
            System.out.println("Player " + p.getName() + " connected, playerId = " + p.getId());
            synchronized (lock) {
                lock.notifyAll();
            }

            return false;
        } else {
            return true;
        }
    }

    /**
     * Sends a message from a player to all other connected clients.
     *
     * @param playerId the ID of the player sending the message
     * @param message the message content
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void sendMessages(int playerId, String message) throws RemoteException {
        for (Player p : this.clientsRemote.keySet()) {
            if (p.getId() == playerId || this.clientsRemote.get(p) == null) {
                continue;
            }
            try {
                this.clientsRemote.get(p).receiveMessage(playerId, message);
            } catch (Exception _) {
            }
        }
    }

    /**
     * Waits for new players to join the game.
     *
     * @param gameName the name of the game
     * @return the game instance with the new players
     */
    public Game waitForNewPlayer(String gameName) {
        this.clientsRemote = new HashMap<>();
        this.state = "waitingNewPlayer";

        this.start = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread(() -> {
            System.out.println("press enter to start the game");
            try {
                reader.readLine();
                this.start = true;
                synchronized (lock) {
                    lock.notifyAll();
                }
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Error while waiting for new player");
            }
        });
        t.start();

        while (!this.start && this.clientsRemote.size() < 3) {
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                // e.printStackTrace();
                System.out.println("Error while waiting for new player");
            }
        }

        this.state = null;

        Player[] players = this.clientsRemote.keySet().toArray(new Player[0]);
        // check if all id are unique
        if (Stream.of(players).map(Player::getId).distinct().count() != players.length) {
            throw new GameRemoteExeceptions("Error while waiting for new player");
        }
        this.game = Game.getInstance(players, gameName, true);

        return this.game;
    }

    /**
     * Waits for all players to connect to the game.
     *
     * @param game the game instance
     */
    public void waitForPlayersConnection(Game game) {
        this.game = game;
        this.clientsRemote = new HashMap<>();
        for (Player player : Game.getInstance().getHumanPlayers()) {
            this.clientsRemote.put(player, null);
        }

        for (Player p : this.clientsRemote.keySet()) {
            System.out.println("Waiting for player " + p.getName() + " to connect");
        }

        this.state = "waitingForPlayers";
        // wait for all players to connect
        while (!allPlayersConnected()) {
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.clientsRemote.size() != 3) {
                System.out.println("Waiting for " + (3 - this.clientsRemote.size()) + " players to connect");
            }
        }

        System.out.println("All players connected");

        this.state = null;
    }

    /**
     * Waits for a specific player to reconnect.
     *
     * @param p the player to wait for
     */
    private void waitForPlayerConnection(Player p) {
        this.state = "waitingForPlayers";
        try {
            this.clientsRemote.get(p).deconnect();
        } catch (RemoteException _) {
        }
        this.clientsRemote.put(p, null);
        try {
            this.sendMessages(p.getId(), "deconnected");
        } catch (RemoteException _) {
        }
        System.out.println("Waiting for player " + p.getName() + " to reconnect");
        while (this.clientsRemote.get(p) == null) {
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                throw new GameRemoteExeceptions("Error while waiting for player " + p.getName() + " to connect");
            }
        }

        System.out.println("Player " + p.getName() + " reconnected");
    }

    /**
     * Checks if all players are connected.
     *
     * @return true if all players are connected, false otherwise
     */
    private boolean allPlayersConnected() {
        for (ClientRemote client : this.clientsRemote.values()) {
            if (client == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Displays an error message to all clients.
     *
     * @param message the error message
     * @param playerId the ID of the player
     */
    public void displayError(String message, int playerId) {
        System.out.println("Error : " + message);
        try {
            for (ClientRemote client : this.clientsRemote.values()) {
                client.displayError(message, playerId);
            }
        } catch (Exception e) {
            throw new GameRemoteExeceptions("Error while displaying error message");
        }
    }

    /**
     * Gets the starting cell ID for a player.
     *
     * @param playerId the ID of the player
     * @return the starting cell ID
     */
    public int getStartingCellId(int playerId) {
        Player p = this.game.getPlayer(playerId);
        this.updateClientGameInstance(p);
        while (true) {
            try {
                System.out.println("Waiting for player " + p.getName() + " to choose a cell");
                int cellId = this.clientsRemote.get(p).getStartingCellId(playerId);
                System.out.println("Player " + p.getName() + " chose cell " + cellId);
                return cellId;
            } catch (Exception e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    /**
     * Gets the command orders from all players.
     *
     * @return a HashMap containing the command orders for each player
     */
    public HashMap<Integer, Command[]> getCommandOrders() {
        HashMap<Integer, Command[]> commandOrders = new HashMap<>();
        int nPlayers = this.game.getPlayers().length;
        CountDownLatch latch = new CountDownLatch(nPlayers);
        for (Player p : this.game.getPlayers()) {
            new Thread(() -> {
                while (true) {
                    try {
                        while (true) {
                            try {
                                this.clientsRemote.get(p).setGameInstance(GameDataConverter.toJson(this.game));
                                break;
                            } catch (Exception e) {
                                this.waitForPlayerConnection(p);
                                continue;
                            }
                        }

                        System.out.println("Waiting for player " + p.getName() + " to enter commands");
                        Command[] commands = this.clientsRemote.get(p).getCommandOrder(p.getId());
                        System.out.println("Player " + p.getName() + " entered commands");
                        synchronized (commandOrders) {
                            commandOrders.put(p.getId(), commands);
                        }
                        latch.countDown();
                        break;
                    } catch (RemoteException e) {
                        this.waitForPlayerConnection(p);
                        continue;
                    }
                }
            }).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new GameRemoteExeceptions("Error while waiting for players to send their commands");
        }

        return commandOrders;
    }

    /**
     * Expands the player's ships to new cells.
     *
     * @param playerId the ID of the player
     * @param nShips the number of ships to expand
     * @return a 2D array where each sub-array contains the cell ID and the number of ships placed on that cell
     */
    public int[][] expand(int playerId, int nShips) {
        Player p = this.game.getPlayer(playerId);
        this.updateClientGameInstance(p);
        while (true) {
            try {
                System.out.println("Player " + p.getName() + " is expanding");
                int[][] result = this.clientsRemote.get(p).expand(playerId, nShips);
                System.out.println("Player " + p.getName() + " expanded");
                System.out.println(Arrays.deepToString(result));
                return result;
            } catch (Exception e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    /**
     * Explores new cells with the player's fleet.
     *
     * @param playerId the ID of the player
     * @param nFleet the number of fleets to explore with
     * @return a 3D array where each sub-array contains the details of the exploration
     */
    public int[][][] explore(int playerId, int nFleet) {
        Player p = this.game.getPlayer(playerId);
        this.updateClientGameInstance(p);
        while (true) {
            try {
                System.out.println("Player " + p.getName() + " is exploring");
                int[][][] result = this.clientsRemote.get(p).explore(playerId, nFleet);
                System.out.println("Player " + p.getName() + " explored");
                System.out.println(Arrays.deepToString(result));
                return result;
            } catch (Exception e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    /**
     * Exterminates enemy ships on specific cells.
     *
     * @param playerId the ID of the player
     * @param nFleet the number of fleets to exterminate
     * @return a 2D array where each sub-array contains the details of the extermination
     */
    public int[][] exterminate(int playerId, int nFleet) {
        Player p = this.game.getPlayer(playerId);
        this.updateClientGameInstance(p);
        while (true) {
            try {
                System.out.println("Player " + p.getName() + " is exterminating");
                int[][] result = this.clientsRemote.get(p).exterminate(playerId, nFleet);
                System.out.println("Player " + p.getName() + " exterminated");
                System.out.println(Arrays.deepToString(result));
                return result;
            } catch (Exception e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    /**
     * Scores a sector for a specific player.
     *
     * @param id the ID of the player
     * @return the ID of the scored sector
     */
    public int score(int id) {
        Player p = this.game.getPlayer(id);
        this.updateClientGameInstance(p);
        while (true) {
            try {
                System.out.println("Player " + p.getName() + " is scoring");
                int score = this.clientsRemote.get(p).score(id);
                System.out.println("Player " + p.getName() + " scored the sector " + score);
                return score;
            } catch (Exception e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    /**
     * Displays the winners of the game.
     *
     * @param winnersIds an array of player IDs representing the winners
     */
    public void displayWinner(int[] winnersIds) {
        this.updateClientGameInstance();
        System.out.println("Winners : " + Arrays.toString(winnersIds));
        for (Player p : this.clientsRemote.keySet()) {
            while (true) {
                try {
                    this.clientsRemote.get(p).displayWinner(winnersIds);
                    break;
                } catch (Exception e) {
                    this.waitForPlayerConnection(p);
                    continue;
                }
            }
        }
    }

    /**
     * Displays a draw message.
     */
    public void displayDraw() {
        this.updateClientGameInstance();
        System.out.println("Draw");
        for (Player p : this.clientsRemote.keySet()) {
            while (true) {
                try {
                    this.clientsRemote.get(p).displayDraw();
                    break;
                } catch (Exception e) {
                    this.waitForPlayerConnection(p);
                    continue;
                }
            }
        }
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        this.updateClientGameInstance();
        System.out.println("Starting the game");
        this.game.playGame();
        System.out.println("Game finished");
        exit(0);
    }

    /**
     * Updates the game instance for all clients.
     */
    private void updateClientGameInstance() {
        this.updateClientGameInstance(null);
    }

    /**
     * Updates the game instance for a specific player.
     *
     * @param player the player to update the game instance for
     */
    private void updateClientGameInstance(Player player) {
        for (Player p : this.clientsRemote.keySet()) {
            if (p != player) {
                new Thread(() -> {
                    while (true) {
                        try {
                            this.clientsRemote.get(p).setGameInstance(GameDataConverter.toJson(this.game));
                            break;
                        } catch (Exception e) {
                            this.waitForPlayerConnection(p);
                            continue;
                        }
                    }
                }).start();
            } else {
                while (true) {
                    try {
                        this.clientsRemote.get(p).setGameInstance(GameDataConverter.toJson(this.game));
                        break;
                    } catch (Exception e) {
                        this.waitForPlayerConnection(p);
                        continue;
                    }
                }
            }
        }
    }
}