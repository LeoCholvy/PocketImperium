package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;

/**
 * The System class represents a system in the game.
 * It contains information about the system's ID, usage status, and level.
 */
public class System {
    private boolean used = false;
    @Expose
    private final int id;
    private static int idCounter = 0;
    @Expose
    private final int level;

    /**
     * Constructs a System instance with the specified level.
     * The ID is automatically assigned based on a static counter.
     *
     * @param level the level of the system
     */
    public System(int level) {
        this.id = idCounter;
        idCounter++;
        this.level = level;
    }

    /**
     * Returns the level of the system.
     *
     * @return the level of the system
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Checks if the system is used.
     *
     * @return true if the system is used, false otherwise
     */
    public boolean isUsed() {
        return this.used;
    }

    /**
     * Sets the used status of the system.
     *
     * @param used the used status to set
     */
    public void setUsed(boolean used) {
        this.used = used;
    }
}