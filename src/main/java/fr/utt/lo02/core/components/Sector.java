package fr.utt.lo02.core.components;

import com.google.gson.annotations.Expose;

public class Sector {
    @Expose
    private final int id;

    public Sector(int id) {
        this.id = id;
    }
}
