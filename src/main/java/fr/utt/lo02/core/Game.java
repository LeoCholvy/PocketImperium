package fr.utt.lo02.core;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.components.*;
import fr.utt.lo02.data.GameDataConverter;

import java.lang.System;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * The class is the core of the game
 * Only one instance of the game can be created
 */
public class Game {
    @Expose
    Area area;
    @Expose
    private Player[] players;
    @Expose
    private int startingPlayerIndex;
    private IOHandler input;
    private static Game instance;
    @Expose
    private int round = 0;

    public Game(IOHandler input, Player[] players) {
        if (instance != null) {
            throw new IllegalStateException("Game already created");
        }
        this.players = players;
        this.area = new Area();
        this.input = input;
        instance = this;
    }
    /**
     * Get the instance of the game
     * @return the instance of the game
     */
    public static Game getInstance() {
        return instance;
    }

    /**
     * Initialize the neighbors of each area
     * Only call this method when recreating the game from json data
     */
    public void initNeighbors() {
        this.area.setNeighbors();
    }

    /**
     * Initialize the ships cell
     * Only call this method when recreating the game from json data
     */
    public void initShipsCells() {
        // assign the cell to the ships with its id
        for (Player player : this.players) {
            for (Ship ship : player.getShips()) {
                ship.initCell();
            }
        }
    }

    public void initSectorsCells() {
        for (Sector sector : this.area.getSectors()) {
            sector.initCells();
        }
    }

    // this two methods are useful when 1 player is dead
    private Player cyclePlayers(int n) {
        if (n == 0) {
            return this.players[this.startingPlayerIndex];
        }
        int p = this.startingPlayerIndex;
        int i = 0;
        int nPlayers = this.players.length;
        while (i < nPlayers) {
            p = (p + 1) % nPlayers;
            if (!this.players[p].isDead()) {
                if (++i == n) {
                    return this.players[p];
                }
            }
        }
        throw new IllegalStateException("No player found");
    }
    private void cycleStartingPlayer() {
        // we assume there is at least 2 players
        int n = this.players.length;
        int i = 0;
        int p = this.startingPlayerIndex;
        while (i < n) {
            p = (p + 1) % n;
            if (!this.players[p].isDead()) {
                this.startingPlayerIndex = p;
                return;
            }
            i++;
        }
    }

    public void init() {
        // chose the starting player randomly
        // we assume no player is dead
        this.startingPlayerIndex = (int) (Math.random() * players.length);
        int n = players.length;
        // this list represent the order how the player will chose the cell to place the ships
        Player[] players_order = new Player[n*2];
        // NOTE : we could have used this.cyclePlayers(n)
        for (int i = 0; i < n; i++) {
            players_order[i] = this.players[(i + startingPlayerIndex) % n];
            players_order[n * 2 - 1 - i] = this.players[(i + startingPlayerIndex) % n];
        }

        // reset the sectors
        this.resetSectors();

        for (Player currentPlayer : players_order) {
            // choose a cell to place the ships (the function will aslo check their aviablity)
            Cell cell = this.placeTwoShips(currentPlayer);
            cell.getSector().setUsed(true);
            // add the ships to the cell
            for (Ship ship : currentPlayer.getAvailableShips(2)) {
                ship.setCell(cell);
            }
        }
    }

    private void resetSectors() {
        for (Sector sector : this.area.getSectors()) {
            sector.setUsed(false);
        }
    }

    public Cell placeTwoShips(Player player) {
        // return this.area.getCell(this.input.placeTwoShips(player));
        Cell cell = this.area.getCell(this.input.getStartingCellId(player.getId()));
        if (!(cell.getSystem() != null && cell.getSystem().getLevel() == 1 && !cell.getSector().isUsed())) {
            this.input.displayError("The Cell must be in an empty sector and have a level 1 system");
            return placeTwoShips(player);
        }
        return cell;
    }

    public Player[] getAlivePlayers() {
        // return this.players;
        // only return alive players
        int n = 0;
        for (Player player : this.players) {
            if (!player.isDead()) {
                n++;
            }
        }
        Player[] alivePlayers = new Player[n];
        int i = 0;
        for (Player player : this.players) {
            if (!player.isDead()) {
                alivePlayers[i] = player;
                i++;
            }
        }
        return alivePlayers;
    }

    public Area getArea() {
        return this.area;
    }

    // only meat for GameDataConverter
    public static void setInstance(Game game) {
        instance = game;
    }

    public Player getPlayer(int playerId) {
        for (Player player : this.players) {
            if (player.getId() == playerId) {
                return player;
            }
        }
        return null;
    }

    private HashMap<Integer, Command[]> phase1() {
        HashMap<Integer, Command[]> orders = this.input.getCommandOrders();
        // check if the orders are valid
        // 1. check if we have every player's id
        for (Player player : this.players) {
            if (!orders.containsKey(player.getId())) {
                this.input.displayError("Player " + player.getName() + " didn't give orders");
                return this.phase1();
            }
        }
        // 2. check if the number of orders is correct and if their is 3 commands with no duplicates
        for (Player player : this.players) {
            Command[] commands = orders.get(player.getId());
            if (commands.length != 3) {
                this.input.displayError("Player " + player.getName() + " must give 3 commands");
                return this.phase1();
            }
            if (Stream.of(commands).distinct().count() != 3) {
                this.input.displayError("Player " + player.getName() + " must give 3 different commands");
                return this.phase1();
            }
        }
        return orders;
    }
    private boolean microPhase2(HashMap<Player,Command> commands) {
        // priority Expand then Explore the Exterminate
        // we start from the starting player
        int p = this.startingPlayerIndex;
        int n = this.getAlivePlayers().length;
        int l;
        //1. Expand
        // l <- number of player who expand
        l = (int) Stream.of(commands.values().toArray()).filter(command -> command == Command.EXPAND).count();
        for (int i = 0; i < n; i++) {
            Player player = this.cyclePlayers(i);
            if (commands.get(player) == Command.EXPAND) {
                player.expand(4-l);
            }
        }
        //2. Explore
        l = (int) Stream.of(commands.values().toArray()).filter(command -> command == Command.EXPLORE).count();
        for (int i = 0; i < n; i++) {
            Player player = this.cyclePlayers(i);
            if (commands.get(player) == Command.EXPLORE) {
                player.explore(4-l);
            }
        }
        //3. Exterminate
        l = (int) Stream.of(commands.values().toArray()).filter(command -> command == Command.EXTERMINATE).count();
        for (int i = 0; i < n; i++) {
            Player player = this.cyclePlayers(i);
            if (commands.get(player) == Command.EXTERMINATE) {
                player.exterminate(4-l);
            }
        }

        //NOTE We should get the order from the config file

        return false;
    }
    private boolean phase2(HashMap<Integer, Command[]> orders) {
        for (int i = 0; i < 3; i++) {
            HashMap<Player, Command> commands = new HashMap<>();
            for (Player player : this.players) {
                commands.put(player, orders.get(player.getId())[i]);
            }
            if (this.microPhase2(commands)) {
                return true;
            }
        }

        return false;
    }
    private void phase3() {
        // sustain ships
        for (Cell cell : this.getArea().getGrid()) {
            Ship[] ships = cell.getShips();
            if (ships.length == 0) {
                continue;
            }
            // raise error if the ships are not all from the same player
            Player c = ships[0].getPlayer();
            for (Ship ship : ships) {
                if (ship.getPlayer() != c) {
                    System.out.println("State of the game when the error occurred :");
                    System.out.println(GameDataConverter.toJson(this));
                    throw new IllegalStateException("The ships in the same cell must be from the same player");
                }
            }

            int maxShips;
            if (cell.getSystem() != null) {
                maxShips = cell.getSystem().getLevel() + 1;
            } else {
                maxShips = 1;
            }

            for (int i = maxShips; i < ships.length; i++) {
                ships[i].setCell(null);
            }
        }
        // score sectors
        for (int i = 0; i < this.getAlivePlayers().length; i++) {
            this.cyclePlayers(i).score();
        }
        // if
    }
    public boolean playRound() {
        this.round++;
        HashMap<Integer, Command[]> orders = this.phase1();
        if (this.phase2(orders)) {
            return true;
        }
        if (this.round == 9) {
            return true;
            // when playRound return true, the game is over, we need to call endGame (final scoring)
        }
        this.phase3();

        return false;
    }
    public boolean playGame() {
        return false;
    }
    public IOHandler getInput() {
        return this.input;
    }

    public void initIO(IOHandler io) {
        this.input = io;
    }
}
