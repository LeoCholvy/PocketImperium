package fr.utt.lo02.core.components;

public class System {
    private boolean used;
    private int id;
    private static int idCounter = 0;
    private int level;
    public System(int level) {
        this.id = idCounter;
        idCounter++;
        this.level = level;
    }
}
