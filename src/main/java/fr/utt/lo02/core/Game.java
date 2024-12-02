package fr.utt.lo02.core;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.components.*;
import fr.utt.lo02.data.DataManipulator;
import fr.utt.lo02.data.GameDataConverter;

import java.lang.System;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * The class is the core of the game.
 * Only one instance of the game can be created.
 */
public class Game {
    @Expose
    Area area;
    @Expose
    private Player[] players;
    @Expose
    private int startingPlayerIndex;
    private static Game instance;
    private IOHandler input;
    @Expose
    private int round = 0;
    private String name = null;

    /**
     * Constructor for the Game class.
     * @param players the array of players in the game
     * @throws IllegalStateException if an instance of the game already exists
     */
    private Game(Player[] players) {
        if (instance != null) {
            throw new IllegalStateException("Game already created");
        }
        // this.input = input;
        this.players = players;
        this.area = new Area();
        instance = this;
        this.initPlayerIterator();
    }

    public void initPlayerIterator() {
        int n = this.players.length;
        for (int i = 0 ; i < n ; i++) {
            this.players[i].setNextPlayer(this.players[(i + 1) % n]);
        }
    }
    public Player getStartingPlayer() {
        return this.players[this.startingPlayerIndex];
    }

    /**
     * Get the instance of the game.
     * @return the instance of the game
     */
    public static Game getInstance() {
        if (instance == null) {
            throw new IllegalGameStateExeceptions("Game not created");
        }
        return instance;
    }

    /**
     * Get the instance of the game.
     * @param players the array of players in the game
     * @return the instance of the game
     * @throws IllegalGameStateExeceptions if an instance of the game already exists
     */
    public static Game getInstance(Player[] players, String name) {
        if (instance != null) {
            throw new IllegalGameStateExeceptions("Game already created");
        }
        Game game = new Game(players);
        game.setName(name);
        return game;
    }
    /**
     * Get the instance of the game.
     * @param json the JSON representation of the game
     * @return the instance of the game
     * When loading the game from JSON data, the neighbors of the areas and the cells of the ships must be initialized.
     * You also need to manually initialize the IOHandlers of the players.
     * @see Game#initIO(IOHandler)
     * @see IOHandler
     */
    public static Game getInstance(String json, String name) {
        Game game = GameDataConverter.fromJson(json, name);
        // game.initIO(io);
        return game;
    }

    public static Game getInstance(String json) {
        return GameDataConverter.fromJson(json, null);
    }

    /**
     * Initialize the neighbors of each area.
     * Only call this method when recreating the game from JSON data.
     */
    public void initNeighbors() {
        // FIXME : this method should be in the Area class
        this.area.setNeighbors();
    }

    /**
     * Initialize the ships' cells.
     * Only call this method when recreating the game from JSON data.
     */
    public void initShipsCells() {
        // assign the cell to the ships with its id
        for (Player player : this.players) {
            for (Ship ship : player.getShips()) {
                ship.initCell();
            }
        }
    }

    /**
     * Initialize the cells of each sector.
     */
    public void initSectorsCells() {
        for (Sector sector : this.area.getSectors()) {
            sector.initCells();
        }
    }

    /**
     * Cycle through players to find the next player.
     * @param n the number of players to cycle through
     * @return the next player
     * @throws IllegalStateException if no player is found
     */
    // private Player cyclePlayers(int n) {
    //     if (n == 0) {
    //         return this.players[this.startingPlayerIndex];
    //     }
    //     int p = this.startingPlayerIndex;
    //     int i = 0;
    //     int nPlayers = this.players.length;
    //     while (i < nPlayers) {
    //         p = (p + 1) % nPlayers;
    //         if (!this.players[p].isDead()) {
    //             if (++i == n) {
    //                 return this.players[p];
    //             }
    //         }
    //     }
    //     throw new IllegalStateException("No player found");
    // }

    /**
     * Cycle to the next starting player.
     */
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
        throw new IllegalGameStateExeceptions("No player found (all player are likely dead), error : the game should have ended");
    }

    /**
     * Initialize the game.
     * Chooses the starting player randomly and resets the sectors.
     */
    private void init() {
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

    /**
     * Reset the sectors to their initial state.
     */
    private void resetSectors() {
        // FIXME : this method should be in the Area class
        for (Sector sector : this.area.getSectors()) {
            sector.setUsed(false);
        }
    }

    /**
     * Place two ships for a player.
     * @param player the player placing the ships
     * @return the cell where the ships are placed
     */
    public Cell placeTwoShips(Player player) {
        int input = this.input.getStartingCellId(player.getId());
        if (!(input >= 0 && input < this.area.getGrid().length)) {
            this.input.displayError("The cell id must be between 0 and " + (this.area.getGrid().length - 1));
            return placeTwoShips(player);
        }
        Cell cell = this.area.getCell(input);
        if (!(cell.getSystem() != null && cell.getSystem().getLevel() == 1 && !cell.getSector().isUsed())) {
            this.input.displayError("The Cell must be in an empty sector and have a level 1 system");
            return placeTwoShips(player);
        }
        return cell;
    }

    /**
     * Get the alive players.
     * @return an array of alive players
     */
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

    /**
     * Get the area of the game.
     * @return the area of the game
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * Set the instance of the game.
     * Only meant for GameDataConverter.
     * @param game the game instance to set
     */
    public static void setInstance(Game game) {
        instance = game;
    }

    /**
     * Get a player by their ID.
     * @param playerId the ID of the player
     * @return the player with the given ID, or null if not found
     */
    public Player getPlayer(int playerId) {
        for (Player player : this.players) {
            if (player.getId() == playerId) {
                return player;
            }
        }
        return null;
    }

    /**
     * Phase 1 of the game.
     * @return a map of player IDs to their commands
     */
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

    /**
     * Micro phase 2 of the game.
     * @param commands a map of players to their commands
     * @return false if the game should continue, true if it should end
     */
    private boolean microPhase2(HashMap<Player, Command> commands) {
        // priority Expand then Explore the Exterminate
        // we start from the starting player
        // int p = this.startingPlayerIndex;
        // int n = this.getAlivePlayers().length;
        int l;
        //1. Expand
        // l <- number of player who expand
        l = (int) Stream.of(commands.values().toArray()).filter(command -> command == Command.EXPAND).count();
        // for (int i = 0; i < n; i++) {
        //     Player player = this.cyclePlayers(i);
        //     if (commands.get(player) == Command.EXPAND) {
        //         player.expand(4 - l);
        //     }
        // }
        Player p = this.getStartingPlayer();
        while (p != null) {
            if (commands.get(p) == Command.EXPAND) {
                p.expand(4 - l);
            }
            p = p.next();
        }

        // 2. Explore
        l = (int) Stream.of(commands.values().toArray()).filter(command -> command == Command.EXPLORE).count();
        // for (int i = 0; i < n; i++) {
        //     Player player = this.cyclePlayers(i);
        //     if (commands.get(player) == Command.EXPLORE) {
        //         player.explore(4 - l);
        //     }
        // }
        p = this.getStartingPlayer();
        while (p != null) {
            if (commands.get(p) == Command.EXPLORE) {
                p.explore(4 - l);
            }
            p = p.next();
        }

        // 3. Exterminate
        l = (int) Stream.of(commands.values().toArray()).filter(command -> command == Command.EXTERMINATE).count();
        // FIXME : generate the order of the players and check alive
        // for (int i = 0; i < n; i++) {
        //     Player player = this.cyclePlayers(i);
        //     if (commands.get(player) == Command.EXTERMINATE) {
        //         player.exterminate(4-l);
        //     }
        // }
        p = this.getStartingPlayer();
        while (p != null) {
            if (commands.get(p) == Command.EXTERMINATE) {
                p.exterminate(4 - l);
                this.checkPlayersDeath();
                if (this.getAlivePlayers().length <= 1) {
                    return true;
                }
            }
            p = p.next();
        }

        // TODO : check if the game should end (or if a player died)
        // NOTE We should get the order from the config file

        return false;
    }

    private void checkPlayersDeath() {
        for (Player player : this.getAlivePlayers()) {
            player.checkDeath();
        }
    }

    /**
     * Phase 2 of the game.
     * @param orders a map of player IDs to their commands
     * @return true if the game should end, false otherwise
     */
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

    /**
     * Phase 3 of the game.
     * Sustains ships and scores sectors.
     */
    private void phase3() {
        // OLD :
        // // sustain ships
        // for (Cell cell : this.getArea().getGrid()) {
        //     Ship[] ships = cell.getShips();
        //     if (ships.length == 0) {
        //         continue;
        //     }
        //     // raise error if the ships are not all from the same player
        //     Player c = ships[0].getPlayer();
        //     for (Ship ship : ships) {
        //         if (ship.getPlayer() != c) {
        //             System.out.println("State of the game when the error occurred :");
        //             System.out.println(GameDataConverter.toJson(this));
        //             throw new IllegalStateException("The ships in the same cell must be from the same player");
        //         }
        //     }
        //
        //     int maxShips;
        //     if (cell.getSystem() != null) {
        //         maxShips = cell.getSystem().getLevel() + 1;
        //     } else {
        //         maxShips = 1;
        //     }
        //
        //     for (int i = maxShips; i < ships.length; i++) {
        //         ships[i].setCell(null);
        //     }
        // }
        // // score sectors
        // for (int i = 0; i < this.getAlivePlayers().length; i++) {
        //     this.cyclePlayers(i).score();
        // }
        // // if

        // NEW :
        this.area.sustainShips();

        this.scoreSectors();
    }

    /**
     * Play a round of the game.
     * @return true if the game should end, false otherwise
     */
    private boolean playRound() {
        this.round++;
        HashMap<Integer, Command[]> orders = this.phase1();
        if (this.phase2(orders)) {
            return true;
        }
        if (this.round >= 9) {
            return true;
            // when playRound return true, the game is over, we need to call endGame (final scoring)
        }
        this.phase3();
        this.cycleStartingPlayer();

        // TODO : save the game state
        DataManipulator.saveGame(this.getName(), GameDataConverter.toJson(this));
        return false;
    }

    public List<Sector> getScorablesSectors() {
        return Stream.of(this.area.getSectors()).filter(Sector::isScorable).toList();
    }

    private void scoreSectors() {
        this.resetSectors();
        Player p = this.getStartingPlayer();
        while (!(p == null || this.getScorablesSectors().isEmpty())) {
            p.score();
            p = p.next(); // NOTE : p.next() will return null if we looped through all the players
        }

        Player triPrimeOwner = this.area.getTriPrimeCell().getOwner();
        if (!this.getScorablesSectors().isEmpty() && triPrimeOwner != null) {
            triPrimeOwner.score();
        }
    }

    /**
     * Play the game.
     * @return false
     */
    public boolean playGame() {
        if (this.round == 0) {
            this.init();
        }
        while (!this.playRound()) {
            // do nothing
        }
        if (this.getName() != null) {
            DataManipulator.saveGame(this.getName(), GameDataConverter.toJson(this));
        }
        this.endGame();
        return false;
    }

    private void endGame() {
        if (this.getAlivePlayers().length == 1) {
            this.getAlivePlayers()[0].score(2);
            this.getInput().displayWinner(new int[]{this.getAlivePlayers()[0].getId()});
        } else if (this.getAlivePlayers().length == 0) {
            this.getInput().displayDraw();
        } else {
            // Final scoring
            this.cycleStartingPlayer();
            this.resetSectors();
            Player p = this.getStartingPlayer();
            while (p != null) {
                p.score(2);
                p = p.next();
            }
            // find the winner
            List<Player> winners = new ArrayList<>();
            int maxScore = -1;
            for (Player player : this.players) {
                if (player.getScore() > maxScore) {
                    winners.clear();
                    winners.add(player);
                    maxScore = player.getScore();
                } else if (player.getScore() == maxScore) {
                    winners.add(player);
                }
            }
            int[] winnerIds = winners.stream().mapToInt(Player::getId).toArray();
            this.getInput().displayWinner(winnerIds);
        }
    }

    /**
     * Get the input handler.
     * @return the input handler
     */
    public IOHandler getInput() {
        return this.input;
    }

    /**
     * Initialize the IO handler.
     * @param io the IO handler to initialize
     */
    public void initIO(IOHandler io) {
        this.input = io;
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public void setName(String name) {
        if (this.name != null) {
            throw new IllegalGameStateExeceptions("The name of the game can only be set once");
        }
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public List<Player> getHumanPlayers() {
        return Stream.of(this.players).filter(Player::isHuman).toList();
    }
}