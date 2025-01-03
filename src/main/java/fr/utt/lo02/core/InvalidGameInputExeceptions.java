package fr.utt.lo02.core;

import fr.utt.lo02.RmiServer.GameExceptions;

public class InvalidGameInputExeceptions extends GameExceptions {
    public InvalidGameInputExeceptions(String message) {
        super(message);
    }
}
