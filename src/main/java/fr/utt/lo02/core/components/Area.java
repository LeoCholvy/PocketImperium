package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.data.DataManipulator;

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

    public void setSectors() {
        Properties properties = DataManipulator.getSectorProperties();
        int nTriPrime = Integer.parseInt(properties.getProperty("numberTriPrimeSector"));
        int nMiddle = Integer.parseInt(properties.getProperty("numberMiddleSector"));
        int nBorder = Integer.parseInt(properties.getProperty("numberBorderSector"));
        int nSector = nTriPrime + nMiddle + nBorder;
        this.sectors = new Sector[nSector];
        int i = 0;
        for(String strSector : properties.getProperty("Sectors").split(" . ")) {
            String[] r = strSector.split(";");
            String type = r[0];
            String strCells = r[1];
            int nCells = strCells.split(",").length;
            Cell[] Cells = new Cell[nCells];
            Sector sector;
            if (type.equals("Border")) {
                sector = new BorderSector();
            } else if (type.equals("Middle")) {
                sector = new MiddleSector();
            } else {
                sector = new TriPrimeSector();
            }
            Cell[] cells = new Cell[nCells];
            for (int j = 0; j < nCells; j++) {
                cells[j] = grid[Integer.parseInt(strCells.split(",")[j])];
            }
            sector.setCells(cells);
            this.sectors[i] = sector;

            i++;
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
