package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.IllegalGameStateExeceptions;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.data.GameDataConverter;

import java.util.List;

/**
 * The Cell class represents a cell in the game area.
 * It contains information about the cell's ID, system, neighbors, and usage status.
 */
public class Cell {
    @Expose
    private final int id;
    @Expose
    private System system = null;
    private Cell[] neighbors;
    @Expose
    private int[] neighborIds;
    private boolean used = false;

    /**
     * Constructs a Cell instance with the specified ID.
     *
     * @param id the ID of the cell
     */
    public Cell(int id) {
        this.id = id;
    }

    /**
     * Returns the ID of the cell.
     *
     * @return the ID of the cell
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the neighbors of the cell.
     *
     * @return an array of neighboring cells
     */
    public Cell[] getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the free neighbors of the cell for the specified player.
     *
     * @param player the player to check for
     * @return a list of free neighboring cells
     */
    public List<Cell> getFrendlyNeighbors(Player player) {
        List<Cell> freeNeighbors = new java.util.ArrayList<>();
        for (Cell neighbor : this.getNeighbors()) {
            if (neighbor.getOwner() == null || neighbor.getOwner() == player) {
                freeNeighbors.add(neighbor);
            }
        }
        return freeNeighbors;
    }

    /**
     * Sets the neighbors of the cell.
     *
     * @param neighbors an array of neighboring cells
     */
    public void setNeighbors(Cell[] neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Returns the IDs of the neighboring cells.
     *
     * @return an array of neighbor IDs
     */
    public int[] getNeighborIds() {
        return this.neighborIds;
    }

    /**
     * Sets the IDs of the neighboring cells.
     *
     * @param neighborIds an array of neighbor IDs
     */
    public void setNeighborIds(int[] neighborIds) {
        this.neighborIds = neighborIds;
    }

    /**
     * Initializes the neighbors of the cell from the neighbor IDs.
     */
    public void initNeighborsFromIds() {
        Cell[] neighbors = new Cell[this.neighborIds.length];
        for (int i = 0; i < this.neighborIds.length; i++) {
            neighbors[i] = Game.getInstance().getArea().getCell(this.neighborIds[i]);
        }
        this.setNeighbors(neighbors);
    }

    /**
     * Returns the system associated with the cell.
     *
     * @return the system associated with the cell
     */
    public System getSystem() {
        return this.system;
    }

    /**
     * Sets the system associated with the cell.
     *
     * @param system the system to associate with the cell
     */
    public void setSystem(System system) {
        this.system = system;
    }

    /**
     * Returns the ships located in the cell.
     *
     * @return an array of ships located in the cell
     */
    public Ship[] getShips() {
        List<Ship> ships = new java.util.ArrayList<>(List.of());
        for (Player player : Game.getInstance().getPlayers()) {
            for (Ship ship : player.getShips()) {
                if (ship.getCell() == this) {
                    ships.add(ship);
                }
            }
        }
        return ships.toArray(new Ship[0]);
    }

    /**
     * Checks if the cell is empty (i.e., contains no ships).
     *
     * @return true if the cell is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.getShips().length == 0;
    }

    /**
     * Returns the sector that the cell belongs to.
     *
     * @return the sector that the cell belongs to, or null if not found
     */
    public Sector getSector() {
        Area area = Game.getInstance().getArea();
        for (Sector sector : area.getSectors()) {
            if (List.of(sector.getCells()).contains(this)) {
                return sector;
            }
        }
        return null;
    }

    /**
     * Returns the owner of the cell (i.e., the player who owns the ships in the cell).
     *
     * @return the owner of the cell, or null if the cell is empty
     * @throws IllegalGameStateExeceptions if ships from different players are found in the same cell
     */
    public Player getOwner() {
        if (this.getShips().length == 0) {
            return null;
        }
        Player owner = this.getShips()[0].getPlayer();
        Ship[] ships = this.getShips();
        for (Ship ship : ships) {
            if (ship.getPlayer() != owner) {
                java.lang.System.out.println("State of the game when the error occurred :");
                java.lang.System.out.println(GameDataConverter.toJson(Game.getInstance()));
                throw new IllegalGameStateExeceptions("The ships in the same cell must be from the same player");
            }
        }
        return owner;
    }

    /**
     * Returns the distance between this cell and another cell.
     * The distance is the number of cells that must be crossed to reach the other cell.
     * The distance is calculated by recursively checking the neighbors of the cell.
     *
     * @param cell the cell to calculate the distance to
     * @param reccursionDepth the maximum depth of the recursion
     * @return the distance between the cells, or null if the distance is greater than the recursion depth
     */
    public Integer distance(Cell cell, int reccursionDepth) {
        if (reccursionDepth == 0) {
            return null;
        }
        if (this == cell) {
            return 0;
        }
        Integer minDistance = null;
        for (Cell neighbor : this.getNeighbors()) {
            Integer distance = neighbor.distance(cell, reccursionDepth - 1);
            if (distance != null) {
                if (minDistance == null || distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance == null ? null : 1 + minDistance;
    }

    /**
     * Checks if the cell is available for the specified player.
     * A cell is available if it is empty or if the player is the owner of the cell.
     *
     * @param player the player to check availability for
     * @return true if the cell is available, false otherwise
     */
    public boolean isAvailable(Player player) {
        return this.isEmpty() || this.getOwner() == player;
    }

    /**
     * Returns an array of available ships in the cell.
     *
     * @param n the number of ships to retrieve
     * @return an array of available ships, or null if there are not enough available ships
     */
    public Ship[] getAvailableShips(int n) {
        if (n == 0) {
            return new Ship[0];
        }
        Ship[] shipsOnCell = this.getShips();
        int i = 0;
        Ship[] availableShips = new Ship[n];
        for (Ship ship : shipsOnCell) {
            if (!ship.isUsed()) {
                availableShips[n - 1] = ship;
                n--;
            }
            if (n == 0) {
                return availableShips;
            }
        }
        return null;
    }

    /**
     * Returns the count of available ships in the cell.
     *
     * @return the count of available ships
     */
    public int getAvailableShipsCount() {
        int count = 0;
        for (Ship ship : this.getShips()) {
            if (!ship.isUsed()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Sustains the ships in the cell.
     * The number of ships that can be sustained is determined by the system level.
     */
    public void sustainShips() {
        Ship[] ships = this.getShips();
        this.getOwner(); // check if all ships are from the same player
        int max = 1;
        if (this.system != null) {
            max += this.system.getLevel();
        }
        for (int i = 0; i < ships.length - max; i++) {
            ships[i].setCell(null);
        }
    }

    /**
     * Resets the systems in the cell.
     */
    public void resetSystems() {
        if (this.system != null) {
            this.system.setUsed(false);
        }
    }

    /**
     * Checks if the cell is used.
     *
     * @return true if the cell is used, false otherwise
     */
    public boolean isUsed() {
        return this.used;
    }

    /**
     * Sets the used status of the cell.
     *
     * @param used the used status to set
     */
    public void setUsed(boolean used) {
        this.used = used;
    }
}