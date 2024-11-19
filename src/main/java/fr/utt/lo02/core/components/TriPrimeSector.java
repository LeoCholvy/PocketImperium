package fr.utt.lo02.core.components;

public class TriPrimeSector extends Sector {
    public TriPrimeSector(int id) {
        super(id);
    }
    @Override
    public void generateSystems() {
        // we assume the sector only have one cell
        this.cells[0].setSystem(new System(3));
    }
}
