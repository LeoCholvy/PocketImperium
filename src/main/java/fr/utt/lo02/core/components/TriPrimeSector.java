package fr.utt.lo02.core.components;

/**
 * The TriPrimeSector class represents a sector of type TRI\_PRIME in the game.
 * It extends the Sector class and initializes its type to TRI\_PRIME.
 */
public class TriPrimeSector extends Sector {

    /**
     * Constructs a TriPrimeSector instance with the specified ID.
     *
     * @param id the ID of the sector
     */
    public TriPrimeSector(int id) {
        super(id);
        this.initType();
    }

    /**
     * Initializes the type of the sector to TRI\_PRIME.
     */
    public void initType() {
        this.type = SectorType.TRI_PRIME;
    }

    /**
     * Generates systems within the sector.
     * Assumes the sector only has one cell and sets its system to level 3.
     */
    @Override
    public void generateSystems() {
        // we assume the sector only have one cell
        this.cells[0].setSystem(new System(3));
    }
}