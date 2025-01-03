package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.Game;

/**
 * The Sector class represents a sector in the game area.
 * It contains information about the sector's ID, usage status, cells, and type.
 */
public class Sector {
    @Expose
    private final int id;
    @Expose
    private boolean used = false;
    @Expose
    private int[] cellIds;
    protected Cell[] cells;
    @Expose
    protected SectorType type;

    /**
     * Constructs a Sector instance with the specified ID.
     *
     * @param id the ID of the sector
     */
    public Sector(int id) {
        this.id = id;
        this.initType();
    }

    /**
     * Initializes the type of the sector.
     * This method should be overridden by subclasses to set the specific type.
     */
    public void initType() {}

    /**
     * Returns the ID of the sector.
     *
     * @return the ID of the sector
     */
    public int getId() {
        return this.id;
    }

    /**
     * Checks if the sector is used.
     *
     * @return true if the sector is used, false otherwise
     */
    public boolean isUsed() {
        return this.used;
    }

    /**
     * Sets the used status of the sector.
     *
     * @param used the used status to set
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Returns the cells in the sector.
     *
     * @return an array of cells in the sector
     */
    public Cell[] getCells() {
        return this.cells;
    }

    /**
     * Sets the cells in the sector and updates the cell IDs.
     *
     * @param cells an array of cells to set in the sector
     */
    public void setCells(Cell[] cells) {
        this.cells = cells;
        this.cellIds = new int[cells.length];
        for (int i = 0; i < cells.length; i++) {
            this.cellIds[i] = cells[i].getId();
        }
    }

    /**
     * Generates systems within the sector.
     * This method should be overridden by subclasses to implement specific system generation logic.
     */
    public void generateSystems() {}

    /**
     * Initializes the cells in the sector from the cell IDs.
     */
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

    /**
     * Returns the type of the sector.
     *
     * @return the type of the sector
     */
    public SectorType getType() {
        return this.type;
    }

    /**
     * Checks if the sector is scorable.
     * A scorable sector has at least one cell with a system and is not used.
     *
     * @return true if the sector is scorable, false otherwise
     */
    public boolean isScorable() {
        Area area = Game.getInstance().getArea();
        if (area.getTriPrimeSector() == this || this.used) {
            return false;
        }
        for (Cell cell : this.cells) {
            if (cell.getSystem() != null && cell.getOwner() != null) {
                return true;
            }
        }
        return false;
    }
}