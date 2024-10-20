package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;

public class System {
    private boolean used;
    @Expose
    private final int id;
    private static int idCounter = 0;
    @Expose
    private final int level;
    public System(int level) {
        this.id = idCounter;
        idCounter++;
        this.level = level;
    }
    public int getLevel() {
        return this.level;
    }
}
