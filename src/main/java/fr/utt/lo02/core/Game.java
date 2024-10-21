package fr.utt.lo02.core;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.components.Area;
import fr.utt.lo02.core.components.Cell;
import fr.utt.lo02.core.components.Sector;
import fr.utt.lo02.core.components.Ship;

/**
 * The class is the core of the game
 * Only one instance of the game can be created
 */
public class Game {
    @Expose
    Area area;
    @Expose
    private Player[] players;
    private int startingPlayerIndex;
    private IOHandler input;
    private static Game instance;

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


    public void init() {
        // chose the starting player randomly
        this.startingPlayerIndex = (int) (Math.random() * players.length);
        int n = players.length;
        // this list represent the order how the player will chose the cell to place the ships
        Player[] players_order = new Player[n*2];
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
        Cell cell = this.area.getCell(this.input.placeTwoShips(player));
        if (!(cell.getSystem() != null && cell.getSystem().getLevel() == 1 && !cell.getSector().isUsed())) {
            this.input.displayError("The Cell must be in an empty sector and have a level 1 system");
            return placeTwoShips(player);
        }
        return cell;
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public Area getArea() {
        return this.area;
    }

    // only meat for GameDataConverter
    public static void setInstance(Game game) {
        instance = game;
    }
}
