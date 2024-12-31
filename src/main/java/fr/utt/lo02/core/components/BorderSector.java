package fr.utt.lo02.core.components;

import fr.utt.lo02.data.DataManipulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The BorderSector class represents a sector located at the border of the game area.
 * It extends the Sector class and initializes its type to BORDER.
 */
public class BorderSector extends Sector {
    /**
     * Constructs a BorderSector instance with the specified ID.
     *
     * @param id the ID of the sector
     */
    public BorderSector(int id) {
        super(id);
        this.initType();
    }

    /**
     * Initializes the type of the sector to BORDER.
     */
    @Override
    public void initType() {
        this.type = SectorType.BORDER;
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