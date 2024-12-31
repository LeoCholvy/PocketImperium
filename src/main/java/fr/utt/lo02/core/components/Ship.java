package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;

import java.util.List;

/**
 * The Ship class represents a ship in the game.
 * It contains information about the ship's ID, cell, and usage status.
 */
public class Ship {
    private boolean used = false;
    @Expose
    private final int id;
    @Expose
    private Integer cellId;
    private Cell cell;

    /**
     * Constructs a Ship instance with the specified ID.
     *
     * @param id the ID of the ship
     */
    public Ship(int id) {
        this.id = id;
    }

    /**
     * Sets the used status of the ship.
     *
     * @param used the used status to set
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Returns the used status of the ship.
     *
     * @return true if the ship is used, false otherwise
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Checks if the ship is available (i.e., not assigned to any cell).
     *
     * @return true if the ship is available, false otherwise
     */
    public boolean isAvailable() {
        return this.cell == null;
    }

    /**
     * Sets the cell of the ship.
     *
     * @param cell the cell to assign to the ship
     */
    public void setCell(Cell cell) {
        this.cell = cell;
        if (cell != null) {
            this.cellId = cell.getId();
        } else {
            this.cellId = null;
        }
    }

    /**
     * Returns the cell assigned to the ship.
     *
     * @return the cell assigned to the ship
     */
    public Cell getCell() {
        return this.cell;
    }

    /**
     * Initializes the cell of the ship.
     * This method is called after the game is loaded from a JSON file.
     */
    public void initCell() {
        if (this.cellId == null) {
            return;
        }
        this.cell = Game.getInstance().getArea().getCell(this.cellId);
    }

    /**
     * Returns the player who owns the ship.
     *
     * @return the player who owns the ship, or null if not found
     */
    public Player getPlayer() {
        for (Player player : Game.getInstance().getAlivePlayers()) {
            if (List.of(player.getShips()).contains(this)) {
                return player;
            }
        }
        return null;
    }
}