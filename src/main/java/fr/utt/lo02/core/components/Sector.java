package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.Game;

// public abstract class Sector {
public class Sector {
    // private static int idCounter = 0;
    @Expose
    private final int id;
    private boolean used;
    @Expose
    private int[] cellIds;
    protected Cell[] cells;

    // public Sector() {
    //     this.id = idCounter;
    //     idCounter++;
    // }
    public Sector(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
    public boolean isUsed() {
        return this.used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }
    public Cell[] getCells() {
        return this.cells;
    }
    public void setCells(Cell[] cells) {
        this.cells = cells;
        // add ids to cellIds
        this.cellIds = new int[cells.length];
        for (int i = 0; i < cells.length; i++) {
            this.cellIds[i] = cells[i].getId();
        }
    }
    // public abstract void generateSystems();
    public void generateSystems() {
        // do nothing
    }
    public void initCells() {
        if (this.cellIds == null) {
            return;
        }
        for (int i = 0; i < this.cellIds.length; i++) {
            if (this.cells == null) {
                this.cells = new Cell[this.cellIds.length];
            }
            this.cells[i] = Game.getInstance().getArea().getCell(this.cellIds[i]);
        }
    }
}
