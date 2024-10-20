package fr.utt.lo02.core.components;

import fr.utt.lo02.core.Player;

public class Ship {
    private static int idCounter = 0;
    private boolean used;
    private int id;

    public Ship() {
        this.id = idCounter;
        idCounter++;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
    public boolean isUsed() {
        return used;
    }
}
