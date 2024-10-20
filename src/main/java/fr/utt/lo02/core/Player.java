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
        ships = new Ship[nShips];
        for (int i = 0; i < nShips; i++) {
            ships[i] = new Ship();
        }
    }
    public String getName() {
        return name;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
}
