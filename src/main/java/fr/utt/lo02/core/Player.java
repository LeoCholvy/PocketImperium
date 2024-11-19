package fr.utt.lo02.core;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.components.Cell;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.core.components.Ship;
import static fr.utt.lo02.data.DataManipulator.getConfigProperties;

import java.util.Arrays;
import java.util.List;

public class Player {
    private static int idCounter = 0;
    @Expose
    private final int id;
    @Expose
    private String name;
    @Expose
    private int score;
    // @Expose
    // private List<Command> commandsOrder;
    @Expose
    private Ship[] ships;
    @Expose
    private boolean dead = false;

    /**
     * Constructor for the Player class.
     * @param name the name of the player
     */
    public Player(String name) {
        // give an unique id to the player
        this.id = idCounter;
        idCounter++;
        this.name = name;
        this.score = 0;
        // get the number of ships per player from the config file
        int nShips = Integer.parseInt(getConfigProperties().getProperty("numberShipsPerPlayer"));
        this.ships = new Ship[nShips];
        // add ships to the player supply
        for (int i = 0; i < nShips; i++) {
            this.ships[i] = new Ship();
        }
    }

    /**
     * Get the name of the player.
     * @return the name of the player
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the score of the player.
     * @return the score of the player
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Set the score of the player.
     * @param score the new score of the player
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Get a string representation of the player.
     * @return the name of the player
     */
    public String toString() {
        return this.name;
    }

    /**
     * Get the available ships of the player.
     * @param n the number of ships to get
     * @return the available ships, or null if the player doesn't have enough ships
     */
    public Ship[] getAvailableShips(int n) {
        Ship[] availableShips = new Ship[n];
        int i = 0;
        Game game = Game.getInstance();
        while (n > 0 && i < this.ships.length) {
            if (this.ships[i].isAvailable()) {
                availableShips[n - 1] = this.ships[i];
                n--;
            }
            i++;
        }
        if (n > 0) {
            return null;
        }
        return availableShips;
    }

    /**
     * Get all ships of the player.
     * @return an array of all ships
     */
    public Ship[] getShips() {
        return this.ships;
    }

    /**
     * Get the ID of the player.
     * @return the ID of the player
     */
    public int getId() {
        return this.id;
    }

    /**
     * Check if the player is dead.
     * @return true if the player is dead, false otherwise
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Set the player's dead status.
     * @param dead the new dead status
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Score the player based on the sector ID.
     */
    public void score () {
        int SectorId = Game.getInstance().getInput().score(this.getId());
        // TODO : check if the input is valid
        // TODO : score the player
    }

    /**
     * Expand the player's ships.
     * @param nShips the number of ships to expand
     */
    public void expand(int nShips) {
        int[][] input = Game.getInstance().getInput().expand(this.getId(), nShips);
        // should be array of [cellId, nShips]
        // TODO : check if the input is valid
        // can't add too much ships
        if (input == null) {
            Game.getInstance().getInput().displayError("Invalid input");
            expand(nShips);
        }
        if (Arrays.stream(input).mapToInt(x -> x[1]).sum() > nShips) {
            Game.getInstance().getInput().displayError("You can't add more ships than you have");
            expand(nShips);
        }
        // need to add ships on player's system
        for (int[] i : input) {
            Game game = Game.getInstance();
            Cell cell = Game.getInstance().getArea().getCell(i[0]);
            if (cell.getSystem() == null) {
                Game.getInstance().getInput().displayError("You can't add ships on a cell without a system");
                expand(nShips);
            }
            if (cell.getOwner() != this) {
                Game.getInstance().getInput().displayError("You can't add ships on a cell that doesn't belong to you");
                expand(nShips);
            }
            Ship[] availableShips = this.getAvailableShips(i[1]);
            if (availableShips == null) {
                Game.getInstance().getInput().displayError("You don't have enough ships");
                expand(nShips);
            }
            for (int j = 0; j < i[1]; j++) {
                availableShips[j].setCell(cell);
            }
        }
        // TODO : expand
    }

    /**
     * Explore with the player's fleet.
     * @param nFleet the number of fleets to explore with
     */
    public void explore(int nFleet) {
        // TODO : check if the input is valid
        // TODO : explore
    }

    /**
     * Exterminate a system with the player's fleet.
     * @param nSystem the number of systems to exterminate
     */
    public void exterminate(int nSystem) {
        // TODO : check if the input is valid
        // TODO : exterminate

        // TODO : check if a player is dead
            // if all are dead : end the game
    }

    /**
     * Get the number of available ships.
     * @return the number of available ships
     */
    public int getNumberAvailableShips() {
        return (int) Arrays.stream(this.ships).filter(Ship::isAvailable).count();
    }
}