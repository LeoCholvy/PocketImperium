package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;

public class Cell {
    @Expose
    private final int id;
    @Expose
    private System system = null;
    private Cell[] neighbors;
    public Cell(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public Cell[] getNeighbors() {
        return neighbors;
    }
    public void setNeighbors(Cell[] neighbors) {
        this.neighbors = neighbors;
    }
    public System getSystem() {
        return system;
    }
    public void setSystem(System system) {
        this.system = system;
    }
}
