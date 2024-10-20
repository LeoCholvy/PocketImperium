package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;

public class Ship {
    private static int idCounter = 0;
    private boolean used;
    @Expose
    private final int id;
    @Expose
    private int cellId;
    private Cell cell;
    public Ship() {
        this.id = idCounter;
        idCounter++;
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
        this.cellId = cell.getId();
    }
    public Cell getCell() {
        return this.cell;
    }

    public void initCell() {
        this.cell = Game.getInstance().getArea().getCell(this.cellId);
    }
}
