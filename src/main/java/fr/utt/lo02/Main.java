package fr.utt.lo02;

import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.System;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        // DEBUG MODE
        if (args.length != 0 && args[0].equals("debug")) {
            Game game = new Game();
            game.start(new Player[]{new Player("Player 1"), new Player("Player 2")});

            return;
        }

        Game game = new Game();
    }
}