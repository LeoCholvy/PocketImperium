package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.IllegalGameStateExeceptions;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.data.GameDataConverter;

import java.util.List;

public class Cell {
    @Expose
    private final int id;
    @Expose
    private System system = null;
    private Cell[] neighbors;
    @Expose
    private int[] neighborIds;
    @Expose
    private boolean used = false;
    public Cell(int id) {
        this.id = id;
    }
    // private int[] neighborIds;
    public int getId() {
        return this.id;
    }
    public Cell[] getNeighbors() {
        return this.neighbors;
    }
    public void setNeighbors(Cell[] neighbors) {
        this.neighbors = neighbors;
    }
    public int[] getNeighborIds() {
        return this.neighborIds;
    }
    public void setNeighborIds(int[] neighborIds) {
        this.neighborIds = neighborIds;
    }
    public void initNeighborsFromIds() {
        Cell[] neighbors = new Cell[this.neighborIds.length];
        for (int i = 0; i < this.neighborIds.length; i++) {
            neighbors[i] = Game.getInstance().getArea().getCell(this.neighborIds[i]);
        }
        this.setNeighbors(neighbors);
    }
    public System getSystem() {
        return this.system;
    }
    public void setSystem(System system) {
        this.system = system;
    }
    public Ship[] getShips() {
        List<Ship> ships = new java.util.ArrayList<>(List.of());
        for (Player player : Game.getInstance().getAlivePlayers()) {
            for (Ship ship : player.getShips()) {
                if (ship.getCell() == this) {
                    ships.add(ship);
                }
            }
        }
        return ships.toArray(new Ship[0]);
    }
    public boolean isEmpty() {
        return this.getShips().length == 0;
    }

    public Sector getSector() {
        Area area = Game.getInstance().getArea();
        for (Sector sector : area.getSectors()) {
            if (List.of(sector.getCells()).contains(this)) {
                return sector;
            }
        }
        return null;
    }

    public Player getOwner() {
        if (this.getShips().length == 0) {
            return null;
        }
        Player owner = this.getShips()[0].getPlayer();
        // check if no other player has ships on this cell
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
     * @param cell The cell to calculate the distance to
     * @param reccursionDepth The maximum depth of the recursion, be careful, you need a recursion depth of 2 for a distance of 1 for example (add 1 to the minimum)
     * @return The distance between the cells or null if the distance is greater than the recursion depth
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

    public boolean isAvailable(Player player) {
        return this.isEmpty() || this.getOwner() == player;
    }
    public Ship[] getAvailableShips(int n) {
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

    public void resetSystems() {
        if (this.system != null) {
            this.system.setUsed(false);
        }
    }
}
