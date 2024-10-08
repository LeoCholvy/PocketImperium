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
            String[] neighbor_ids = neighbors.getProperty(String.valueOf(i)).split(",");
            Cell[] neighbor_cells = new Cell[neighbor_ids.length];
            for (int j = 0; j < neighbor_ids.length; j++) {
                neighbor_cells[j] = grid[Integer.parseInt(neighbor_ids[j])];
            }
            grid[i].setNeighbors(neighbor_cells);
        }
    }


}
