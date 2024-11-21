package fr.utt.lo02.core;

import fr.utt.lo02.data.GameDataConverter;

public class IllegalGameStateExeceptions extends RuntimeException {
    public IllegalGameStateExeceptions(String message) {
        // TODO : display the state of the game ?
        super(message);
    }
}
