package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;

import java.util.List;

public class Ship {
    // private static int idCounter = 0;
    @Expose
    private boolean used;
    @Expose
    private final int id;
    @Expose
    private Integer cellId;
    private Cell cell;
    // public Ship() {
    //     this.id = idCounter;
    //     idCounter++;
    // }
    public Ship(int id) {
        this.id = id;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
    public boolean isUsed() {
        return used;
    }
    public boolean isAvailable() {
        return this.cell == null;
    }
    public void setCell(Cell cell) {
        this.cell = cell;
        if (cell != null) {
            this.cellId = cell.getId();
        } else {
            this.cellId = null;
        }
    }
    public Cell getCell() {
        return this.cell;
    }

    /**
     * Initialize the cell of the ship
     * This method is called after the game is loaded from a json file
     */
    public void initCell() {
        if (this.cellId == null) {
            return;
        }
        this.cell = Game.getInstance().getArea().getCell(this.cellId);
    }

    public Player getPlayer() {
        for (Player player : Game.getInstance().getAlivePlayers()) {
            if (List.of(player.getShips()).contains(this)) {
                return player;
            }
        }
        return null;
    }
}
