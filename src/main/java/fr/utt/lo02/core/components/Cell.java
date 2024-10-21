package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;

import java.util.List;

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
        return this.id;
    }
    public Cell[] getNeighbors() {
        return this.neighbors;
    }
    public void setNeighbors(Cell[] neighbors) {
        this.neighbors = neighbors;
    }
    public System getSystem() {
        return this.system;
    }
    public void setSystem(System system) {
        this.system = system;
    }
    public Ship[] getShips() {
        List<Ship> ships = List.of();
        for (Player player : Game.getInstance().getPlayers()) {
            for (Ship ship : player.getShips()) {
                if (ship.getCell() == this) {
                    ships.add(ship);
                }
            }
        }
        return ships.toArray(new Ship[0]);
    }
    public Player getControlPlayer() {
        return this.getShips()[0].getPlayer();
    }
    public boolean isEmpty() {
        return this.getShips().length == 0;
    }
}
