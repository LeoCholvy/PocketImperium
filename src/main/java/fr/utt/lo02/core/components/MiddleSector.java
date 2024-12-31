package fr.utt.lo02.core.components;

import fr.utt.lo02.data.DataManipulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The MiddleSector class represents a sector located in the middle of the game area.
 * It extends the Sector class and initializes its type to MIDDLE.
 */
public class MiddleSector extends Sector {
    /**
     * Constructs a MiddleSector instance with the specified ID.
     *
     * @param id the ID of the sector
     */
    public MiddleSector(int id) {
        super(id);
        this.initType();
    }

    /**
     * Initializes the type of the sector to MIDDLE.
     */
    @Override
    public void initType() {
        this.type = SectorType.MIDDLE;
    }

    /**
     * Generates systems within the sector based on the configuration properties.
     * The number of level 1 and level 2 systems is determined by the properties.
     */
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