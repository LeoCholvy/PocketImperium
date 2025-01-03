package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.IllegalGameStateExeceptions;
import fr.utt.lo02.core.InvalidGameInputExeceptions;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.data.DataManipulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The Area class represents the game area, which consists of a grid of cells and sectors.
 * It provides methods to initialize the area, set neighbors, set sectors, generate systems, and retrieve cells and sectors.
 */
public class Area {
    @Expose
    private Cell[] grid;
    @Expose
    private Sector[] sectors;

    /**
     * Constructs an Area instance and initializes the grid and sectors.
     *
     * @param isDefaultMap a boolean indicating whether to use the default map configuration
     */
    public Area(boolean isDefaultMap) {
        Properties neighbors = DataManipulator.getMapProperties();
        int n = neighbors.size();
        this.grid = new Cell[n];
        for (int i = 0; i < n; i++) {
            grid[i] = new Cell(i);
        }

        // set neighbors
        this.setNeighborsFromConfig();

        // set sectors
        this.setSectors();
        // generate system
        if (!isDefaultMap) {
            this.generateSystems();
        } else {
            this.getCell(1).setSystem(new System(2));
            this.getCell(3).setSystem(new System(1));
            this.getCell(5).setSystem(new System(1));
            this.getCell(6).setSystem(new System(1));
            this.getCell(7).setSystem(new System(1));
            this.getCell(9).setSystem(new System(1));
            this.getCell(13).setSystem(new System(1));
            this.getCell(14).setSystem(new System(2));
            this.getCell(17).setSystem(new System(2));
            this.getCell(18).setSystem(new System(2));
            this.getCell(25).setSystem(new System(1));
            this.getCell(20).setSystem(new System(1));
            this.getCell(0).setSystem(new System(3));
            this.getCell(27).setSystem(new System(2));
            this.getCell(22).setSystem(new System(1));
            this.getCell(29).setSystem(new System(1));
            this.getCell(30).setSystem(new System(1));
            this.getCell(31).setSystem(new System(1));
            this.getCell(33).setSystem(new System(1));
            this.getCell(36).setSystem(new System(2));
            this.getCell(38).setSystem(new System(2));
            this.getCell(40).setSystem(new System(2));
            this.getCell(44).setSystem(new System(1));
            this.getCell(45).setSystem(new System(1));
            this.getCell(46).setSystem(new System(1));
        }
    }

    /**
     * Sets the neighbors for each cell in the grid based on the configuration properties.
     */
    public void setNeighborsFromConfig() {
        Properties neighbors = DataManipulator.getMapProperties();
        int n = neighbors.size();
        for (int i = 0; i < n; i++) {
            String[] neighborIds = neighbors.getProperty(String.valueOf(i)).split(",");
            Cell[] neighborCells = new Cell[neighborIds.length];
            int[] neighborIdsInt = new int[neighborIds.length];
            for (int j = 0; j < neighborIds.length; j++) {
                neighborCells[j] = grid[Integer.parseInt(neighborIds[j])];
                neighborIdsInt[j] = Integer.parseInt(neighborIds[j]);
            }
            this.grid[i].setNeighborIds(neighborIdsInt);
            this.grid[i].setNeighbors(neighborCells);
        }
    }

    /**
     * Sets the neighbors for each cell in the grid.
     */
    public void setNeighbors() {
        int n = grid.length;
        for (Cell cell : grid) {
            cell.initNeighborsFromIds();
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
            // FIXME : Use "." instead of " . " and delete all the " " from the string before splitting
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
                    sector = new BorderSector(i);
                } else if (type.equals("Middle")) {
                    sector = new MiddleSector(i);
                } else {
                    sector = new TriPrimeSector(i);
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
            throw new IllegalGameStateExeceptions("Error setting sectors, the config file may be corrupted");
        }
    }

    /**
     * Generates systems for each sector in the area.
     */
    public void generateSystems() {
        for (Sector sector : sectors) {
            sector.generateSystems();
        }
    }

    /**
     * Retrieves a cell by its ID.
     *
     * @param id the ID of the cell
     * @return the cell with the specified ID
     * @throws IllegalGameStateExeceptions if the cell ID is out of bounds
     */
    public Cell getCell(int id) {
        if (id < 0 || id >= grid.length) {
            throw new IllegalGameStateExeceptions("Cell id out of bounds");
        }
        return grid[id];
    }

    /**
     * Retrieves the grid of cells.
     *
     * @return an array of cells representing the grid
     */
    public Cell[] getGrid() {
        return grid;
    }

    /**
     * Retrieves the sectors in the area.
     *
     * @return an array of sectors
     */
    public Sector[] getSectors() {
        return sectors;
    }

    /**
     * Retrieves the cell in the TriPrime sector.
     *
     * @return the cell in the TriPrime sector
     * @throws IllegalGameStateExeceptions if there is not exactly one cell in the TriPrime sector
     */
    public Cell getTriPrimeCell() {
        List<Cell> cells = new java.util.ArrayList<>(List.of());
        for (Sector sector : sectors) {
            if (sector.getType() == SectorType.TRI_PRIME) {
                cells.addAll(List.of(sector.getCells()));
            }
        }
        if (cells.size() != 1) {
            throw new IllegalGameStateExeceptions("TriPrimeSector should have only one cell");
        }
        return cells.get(0);
    }

    /**
     * Sustains ships in all cells of the grid.
     */
    public void sustainShips() {
        for (Cell cell : grid) {
            cell.sustainShips();
        }
    }

    /**
     * Retrieves the TriPrime sector.
     *
     * @return the TriPrime sector
     * @throws IllegalGameStateExeceptions if there is not exactly one TriPrime sector
     */
    public Sector getTriPrimeSector() {
        List<Sector> triPrimes = new ArrayList<>();
        for (Sector sector : this.sectors) {
            if (sector.getType() == SectorType.TRI_PRIME) {
                triPrimes.add(sector);
            }
        }
        if (triPrimes.size() != 1) {
            throw new IllegalGameStateExeceptions("TriPrimeSector should have only one sector");
        }
        return triPrimes.getFirst();
    }

    /**
     * Retrieves a sector by its ID.
     *
     * @param id the ID of the sector
     * @return the sector with the specified ID
     * @throws InvalidGameInputExeceptions if the sector is not found
     */
    public Sector getSector(int id) throws InvalidGameInputExeceptions {
        for (Sector sector : this.sectors) {
            if (sector.getId() == id) {
                return sector;
            }
        }
        throw new InvalidGameInputExeceptions("Sector not found");
    }

    /**
     * Resets the systems in all cells of the grid.
     */
    public void resetSystems() {
        for (Cell cell : this.grid) {
            cell.resetSystems();
        }
    }

    /**
     * Retrieves the Middle sector.
     *
     * @return the Middle sector
     * @throws IllegalGameStateExeceptions if there is not exactly one Middle sector
     */
    public Cell[] getCells() {
        return grid;
    }

    /**
     * Retrieves the Middle sector.
     *
     * @return the Middle sector
     * @throws IllegalGameStateExeceptions if there is not exactly one Middle sector
     */
    public List<Cell> getEnemyCells(Player player) {
        List<Cell> enemyCells = new ArrayList<>();
        for (Cell cell : grid) {
            if (cell.getOwner() != player && cell.getOwner() != null && cell.getSystem() != null) {
                enemyCells.add(cell);
            }
        }
        return enemyCells;
    }
}