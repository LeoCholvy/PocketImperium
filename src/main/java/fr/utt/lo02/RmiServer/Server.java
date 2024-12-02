package fr.utt.lo02.RmiServer;

import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.data.GameDataConverter;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import static java.lang.System.exit;

public class Server extends UnicastRemoteObject implements ServerRemote, IOHandler {
    private String state = null;
    private Game game;
    HashMap<Player, ClientRemote> clientsRemote;


    public void ping() throws RemoteException {
        // Do nothing
    }

    public boolean registerClient(ClientRemote client) throws RemoteException {
        String name = client.getUserName();
        if (this.state.equals("waitingForPlayers")) {
            for (Player p : this.clientsRemote.keySet()) {
                if (p.getName().equals(name)) {
                    if (this.clientsRemote.get(p) != null) {
                        return true;
                    }
                    client.setPlayerId(p.getId());
                    System.out.println("Player " + p.getName() + " connected, playerId = " + p.getId());
                    this.clientsRemote.put(p, client);
                    return false;
                }
            }
            return true;
        } else if (this.state.equals("waitingNewPlayer")) {
            // Player newPlayer = new Player(name, this.clientsRemote.size());
            // this.clientsRemote.put(newPlayer, client);
            return false;
        } else {
            return true;
        }
    }



    public Server() throws RemoteException {
        super();
        this.startServer();
    }


    private void startServer() {
        try {
            LocateRegistry.createRegistry(1099);

            String url = "rmi://localhost:1099/PocketImperium";
            java.rmi.Naming.rebind(url, this);
            System.out.println("Server started at " + url);
            // display the server ip address
            java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
            System.out.println("Server IP Address: " + addr.getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while creating the server");
        }
    }

    public void waitForNewPlayer() {
        this.state = "waitingForPlayer";
    }

    public void waitForPlayersConnection(Game game) {
        this.game = game;
        this.game.initIO(this);
        this.state = "waitingForPlayers";
        this.clientsRemote = new HashMap<>();
        for (Player player : Game.getInstance().getHumanPlayers()) {
            this.clientsRemote.put(player, null);
        }

        for (Player p : this.clientsRemote.keySet()) {
            System.out.println("Waiting for player " + p.getName() + " to connect");
        }


        // wait for all players to connect
        while(!allPlayersConnected()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error while waiting for players to connect");
            }
        }

        System.out.println("All players connected");

        this.state = null;
    }

    private void waitForPlayerConnection(Player p) {
        this.state = "waitingForPlayer";
        System.out.println("Waiting for player " + p.getName() + " to reconnect");
        while (this.clientsRemote.get(p) == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new GameRemoteExeceptions("Error while waiting for player " + p.getName() + " to connect");
            }
        }
    }


    private boolean allPlayersConnected() {
        for (ClientRemote client : this.clientsRemote.values()) {
            if (client == null) {
                return false;
            }
        }
        return true;
    }


    public void displayError(String message) {
        try {
            for (ClientRemote client : this.clientsRemote.values()) {
                client.displayError(message);
            }
        } catch (RemoteException e) {
            throw new GameRemoteExeceptions("Error while displaying error message");
        }
    }

    public int getStartingCellId(int playerId) {
        this.updateClientGameInstance();
        Player p = this.game.getPlayer(playerId);
        while (true) {
            try {
                return this.clientsRemote.get(p).getStartingCellId(playerId);
            } catch (RemoteException e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    public HashMap<Integer, Command[]> getCommandOrders() {
        this.updateClientGameInstance();
        HashMap<Integer, Command[]> commandOrders = new HashMap<>();
        int nPlayers = this.game.getPlayers().length;
        CountDownLatch latch = new CountDownLatch(nPlayers);
        for (Player p : this.game.getPlayers()) {
            new Thread(() -> {
                while (true) {
                    try {
                        commandOrders.put(p.getId(), this.clientsRemote.get(p).getCommandOrder(p.getId()));
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

    public int[][] expand(int playerId, int nShips) {
        this.updateClientGameInstance();
        Player p = this.game.getPlayer(playerId);
        while (true) {
            try {
                return this.clientsRemote.get(p).expand(playerId, nShips);
            } catch (RemoteException e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    public int[][][] explore(int playerId, int nFleet) {
        this.updateClientGameInstance();
        Player p = this.game.getPlayer(playerId);
        while (true) {
            try {
                return this.clientsRemote.get(p).explore(playerId, nFleet);
            } catch (RemoteException e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    public int[][] exterminate(int playerId, int nFleet) {
        this.updateClientGameInstance();
        Player p = this.game.getPlayer(playerId);
        while (true) {
            try {
                return this.clientsRemote.get(p).exterminate(playerId, nFleet);
            } catch (RemoteException e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    public int score(int id) {
        this.updateClientGameInstance();
        Player p = this.game.getPlayer(id);
        while (true) {
            try {
                return this.clientsRemote.get(p).score(id);
            } catch (RemoteException e) {
                this.waitForPlayerConnection(p);
                continue;
            }
        }
    }

    public void displayWinner(int[] winnersIds) {
        this.updateClientGameInstance();
        for (int id : winnersIds) {
            Player p = this.game.getPlayer(id);
            while (true) {
                try {
                    this.clientsRemote.get(p).displayWinner(winnersIds);
                    break;
                } catch (RemoteException e) {
                    this.waitForPlayerConnection(p);
                    continue;
                }
            }
        }
    }

    public void displayDraw() {
        this.updateClientGameInstance();
        for (Player p : this.clientsRemote.keySet()) {
            while (true) {
                try {
                    this.clientsRemote.get(p).displayDraw();
                    break;
                } catch (RemoteException e) {
                    this.waitForPlayerConnection(p);
                    continue;
                }
            }
        }
    }

    public void startGame() {
        this.updateClientGameInstance();
        System.out.println("Starting the game");
        this.game.playGame();
        exit(0);
    }

    private void updateClientGameInstance() {
        for (Player p : this.clientsRemote.keySet()) {
            while (true) {
                try {
                    this.clientsRemote.get(p).setGameInstance(GameDataConverter.toJson(this.game));
                    break;
                } catch (RemoteException e) {
                    this.waitForPlayerConnection(p);
                    continue;
                }
            }
        }
    }
}
