package fr.utt.lo02.core.components;

import java.util.Properties;

public class Area {
    private Cell[] grid;
    // private Sector[] sectors;
    public Area(Properties neighbors) {
        int n = neighbors.size();
        grid = new Cell[n];
        for (int i = 0; i < n; i++) {
            grid[i] = new Cell(i);
        }

        // set neighbors
        for (int i = 0; i < n; i++) {
            String[] neighborIds = neighbors.getProperty(String.valueOf(i)).split(",");
            Cell[] neighborCells = new Cell[neighborIds.length];
            for (int j = 0; j < neighborIds.length; j++) {
                neighborCells[j] = grid[Integer.parseInt(neighborIds[j])];
            }
            grid[i].setNeighbors(neighborCells);
        }
    }

    public Cell getCell(int id) {
        return grid[id];
    }
}
