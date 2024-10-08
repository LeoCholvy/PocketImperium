package fr.utt.lo02.core.components;

public class Cell {
    private final int id;
    // private System system = null;
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
}
