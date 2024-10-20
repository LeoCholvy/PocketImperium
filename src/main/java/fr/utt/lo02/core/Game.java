package fr.utt.lo02.core;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.components.Area;
import fr.utt.lo02.core.components.Cell;
import fr.utt.lo02.core.components.Ship;

import java.util.List;

public class Game {
    @Expose
    Area area;
    @Expose
    private Player[] players;
    private List<Ship> currentFleet;
    private int startingPlayerIndex;
    private IOHandler input;
    private static Game instance;

    public Game(IOHandler input, Player[] players) {
        this.players = players;
        this.area = new Area();
        this.input = input;
        instance = this;
    }

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
    public void initShipsCell() {
        for (Player player : this.players) {
            for (Ship ship : player.getShips()) {
                ship.initCell();
            }
        }
    }


    public void init() {
        this.startingPlayerIndex = (int) (Math.random() * players.length);
        int n = players.length;
        Player[] players_order = new Player[n*2];
        for (int i = 0; i < n; i++) {
            players_order[i] = this.players[(i + startingPlayerIndex) % n];
            players_order[n*2 - 1 - i] = this.players[(i + startingPlayerIndex) % n];
        }
        for (Player currentPlayer : players_order) {
            Cell cell = this.placeTwoShips(currentPlayer);
            for (Ship ship : currentPlayer.getAvailableShips(2)) {
                ship.setCell(cell);
            }
        }
    }

    public Cell placeTwoShips(Player player) {
        // return this.area.getCell(this.input.placeTwoShips(player));
        Cell cell = this.area.getCell(this.input.placeTwoShips(player));
        if (!(cell.isEmptyAndHaveSystem() && cell.getSystem().getLevel() == 1)) {
            this.input.displayError("The Cell must be empty and have a system");
            return placeTwoShips(player);
        }
        // TODO check sector also
        return cell;
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public Area getArea() {
        return this.area;
    }
}
