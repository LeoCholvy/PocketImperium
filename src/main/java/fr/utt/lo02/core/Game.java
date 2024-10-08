package fr.utt.lo02.core;

import fr.utt.lo02.core.components.Area;
import fr.utt.lo02.data.Data_manipulator;

import java.util.Properties;
import static fr.utt.lo02.data.Data_manipulator.loadProperties;

public class Game {
    Area area;
    public void start() {
        Properties properties = loadProperties();
        area = new Area(properties);

    }
}
