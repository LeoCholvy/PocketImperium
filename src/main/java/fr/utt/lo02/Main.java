package fr.utt.lo02;

import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.data.DataManipulator;
import fr.utt.lo02.data.GameDataConverter;

import javax.xml.crypto.Data;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        // DEBUG MODE
        if (args.length != 0 && args[0].equals("debug")) {
            Game game = new Game();
            game.start(new Player[]{new Player("Player 1"), new Player("Player 2")});

            System.out.println(GameDataConverter.toJson(game));

            Game game_copy = GameDataConverter.fromJson(GameDataConverter.toJson(game));
            // DataManipulator.getSavesList();

            return;
        }

        Game game = new Game();
    }
}