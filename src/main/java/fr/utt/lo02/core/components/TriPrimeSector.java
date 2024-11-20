package fr.utt.lo02.core.components;

public class TriPrimeSector extends Sector {
    public TriPrimeSector(int id) {
        super(id);
        this.initType();
    }
    public void initType() {
        this.type = SectorType.TRI_PRIME;
    }
    @Override
    public void generateSystems() {
        // we assume the sector only have one cell
        this.cells[0].setSystem(new System(3));
    }
}
