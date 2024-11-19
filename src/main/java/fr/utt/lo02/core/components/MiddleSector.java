package fr.utt.lo02.core.components;

import fr.utt.lo02.data.DataManipulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MiddleSector extends Sector {
    public MiddleSector(int id) {
        super(id);
    }
    @Override
    public void generateSystems() {
        Properties properties = DataManipulator.getSectorProperties();
        int nLvl1 = Integer.parseInt(properties.getProperty("SectorSystemsLevel1"));
        int nLvl2 = Integer.parseInt(properties.getProperty("SectorSystemsLevel2"));
        ArrayList<Cell> availableCells = new ArrayList<>(List.of(this.getCells()));
        for (int i = 0; i < nLvl1; i++) {
            availableCells.remove((int) (Math.random() * availableCells.size())).setSystem(new System(1));
        }
        for (int i = 0; i < nLvl2; i++) {
            availableCells.remove((int) (Math.random() * availableCells.size())).setSystem(new System(2));
        }
    }
}
