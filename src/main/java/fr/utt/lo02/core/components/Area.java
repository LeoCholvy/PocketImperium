package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.data.DataManipulator;

import java.lang.System;
import java.util.Properties;

public class Area {
    @Expose
    private Cell[] grid;
    @Expose
    private Sector[] sectors;
    // private Sector[] sectors;
    public Area() {
        Properties neighbors = DataManipulator.getMapProperties();
        int n = neighbors.size();
        this.grid = new Cell[n];
        for (int i = 0; i < n; i++) {
            grid[i] = new Cell(i);
        }

        // set neighbors
        this.setNeighbors();

        // set sectors
        this.setSectors();
        // generate system
        this.generateSystems();
    }

    public void setNeighbors() {
        // FIXME: générer neighborsIds et pas neighbors ET ENSUITE appeller initNeighbors de chaque cellules
        Properties neighbors = DataManipulator.getMapProperties();
        int n = neighbors.size();
        for (int i = 0; i < n; i++) {
            String[] neighborIds = neighbors.getProperty(String.valueOf(i)).split(",");
            Cell[] neighborCells = new Cell[neighborIds.length];
            for (int j = 0; j < neighborIds.length; j++) {
                neighborCells[j] = grid[Integer.parseInt(neighborIds[j])];
            }
            this.grid[i].setNeighbors(neighborCells);
        }
    }

/**
 * Sets the sectors for the area based on properties retrieved from the DataManipulator.
 * The sectors are categorized into TriPrime, Middle, and Border sectors.
 * Each sector is assigned a set of cells based on the properties.
 */
public void setSectors() {
    try {
        // Retrieve sector properties
        Properties properties = DataManipulator.getSectorProperties();

        // Get the number of each type of sector
        int nTriPrime = Integer.parseInt(properties.getProperty("numberTriPrimeSector"));
        int nMiddle = Integer.parseInt(properties.getProperty("numberMiddleSector"));
        int nBorder = Integer.parseInt(properties.getProperty("numberBorderSector"));

        // Calculate the total number of sectors
        int nSector = nTriPrime + nMiddle + nBorder;

        // Initialize the sectors array
        this.sectors = new Sector[nSector];
        int i = 0;

        // Iterate over each sector definition in the properties
        for(String strSector : properties.getProperty("Sectors").split(" . ")) {
            // Split the sector definition into type and cells
            String[] r = strSector.split(";");
            String type = r[0];
            String strCells = r[1];

            // Determine the number of cells in the sector
            int nCells = strCells.split(",").length;
            Cell[] Cells = new Cell[nCells];

            // Create the appropriate sector type
            Sector sector;
            if (type.equals("Border")) {
                sector = new BorderSector();
            } else if (type.equals("Middle")) {
                sector = new MiddleSector();
            } else {
                sector = new TriPrimeSector();
            }

            // Assign cells to the sector
            Cell[] cells = new Cell[nCells];
            for (int j = 0; j < nCells; j++) {
                cells[j] = grid[Integer.parseInt(strCells.split(",")[j])];
            }
            sector.setCells(cells);

            // Add the sector to the sectors array
            this.sectors[i] = sector;
            i++;
        }
    } catch (Exception e) {
        System.out.println("Error in setSectors, the properties file is probably missing or corrupted.");
        e.printStackTrace();
    }
}

    public void generateSystems() {
        for (Sector sector : sectors) {
            sector.generateSystems();
        }
    }

    public Cell getCell(int id) {
        return grid[id];
    }
    public Cell[] getGrid() {
        return grid;
    }
    public Sector[] getSectors() {
        return sectors;
    }
}
