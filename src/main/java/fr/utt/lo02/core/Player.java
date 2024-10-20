package fr.utt.lo02.core;

import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.core.components.Ship;
import static fr.utt.lo02.data.DataManipulator.getConfigProperties;
import java.util.List;

public class Player {
    private String name;
    private int score;
    private List<Command> commandsOrder;
    private Ship[] ships;
    public Player(String name) {
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
