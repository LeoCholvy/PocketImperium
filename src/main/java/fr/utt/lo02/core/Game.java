package fr.utt.lo02.core;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.components.Area;
import fr.utt.lo02.core.components.System;

import java.util.Properties;
import static fr.utt.lo02.data.DataManipulator.getMapProperties;

public class Game {
    @Expose
    Area area;
    @Expose
    private Player[] players;
    public void start(Player[] players) {
        area = new Area();

        //     FEUUUUUUUUUUUUUUUR
        area.getCell(12).setSystem(new System(1));

        this.players = players;
    }
    public void setNeighbors() {
        this.area.setNeighbors();
    }
}
