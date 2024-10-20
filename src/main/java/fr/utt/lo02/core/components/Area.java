package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.data.DataManipulator;

import java.util.Properties;

public class Area {
    @Expose
    private Cell[] grid;
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

        // TODO set systems and sectors
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

    public Cell getCell(int id) {
        return grid[id];
    }
    public Cell[] getGrid() {
        return grid;
    }
}
