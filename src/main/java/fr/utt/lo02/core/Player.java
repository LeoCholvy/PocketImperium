package fr.utt.lo02.core;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.core.components.Ship;
import static fr.utt.lo02.data.DataManipulator.getConfigProperties;
import java.util.List;

public class Player {
    private static int idCounter = 0;
    @Expose
    private final int id;
    @Expose
    private String name;
    @Expose
    private int score;
    @Expose
    private List<Command> commandsOrder;
    @Expose
    private Ship[] ships;
    public Player(String name) {
        this.id = idCounter;
        idCounter++;
        this.name = name;
        this.score = 0;
        int nShips = Integer.parseInt(getConfigProperties().getProperty("numberShipsPerPlayer"));
        this.ships = new Ship[nShips];
        for (int i = 0; i < nShips; i++) {
            this.ships[i] = new Ship();
        }
    }
    public String getName() {
        return this.name;
    }
    public int getScore() {
        return this.score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public String toString() {
        return this.name;
    }
    public Ship[] getAvailableShips(int n) {
        Ship[] availableShips = new Ship[n];
        int i = 0;
        while (n > 0 && i < this.ships.length) {
            if (this.ships[i].isAvailable()) {
                availableShips[n - 1] = this.ships[i];
                n--;
            }
            i++;
        }
        if (n > 0) {
            return null;
        }
        return availableShips;
    }
    public Ship[] getShips() {
        return this.ships;
    }
}
