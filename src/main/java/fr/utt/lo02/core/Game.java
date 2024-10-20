package fr.utt.lo02.core;

import fr.utt.lo02.core.components.Area;
import fr.utt.lo02.core.components.System;

import java.util.Properties;
import static fr.utt.lo02.data.DataManipulator.getMapProperties;

public class Game {
    Area area;
    private Player[] players;
    public void start(Player[] players) {
        Properties mapProperties = getMapProperties();
        area = new Area(mapProperties);

        //     FEUUUUUUUUUUUUUUUR
        area.getCell(12).setSystem(new System(1));

        this.players = players;
    }
}
