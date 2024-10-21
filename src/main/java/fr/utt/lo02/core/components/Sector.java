package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;

public class Sector {
    @Expose
    private final int id;
    private boolean used;
    private Cell[] cells;

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
    }
}
