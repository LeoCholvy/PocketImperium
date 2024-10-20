package fr.utt.lo02;

import fr.utt.lo02.IO.IOMode;
import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.data.GameDataConverter;

public class Main {
    public static void main(String[] args) {
        IOHandler io;
        if (args[0].equals("CLI")) {
            io = new IOHandler(IOMode.CLI);
        } else if (args[0].equals("GUI")) {
            io = new IOHandler(IOMode.GUI);
        } else {
            io = new IOHandler(IOMode.GUI);
        }

        // DEBUG MODE
        if (args.length >= 2 && args[1].equals("debug")) {
            Game game = new Game(io, new Player[] {new Player("Leo"), new Player("Dodo")});
            game.init();

            String json = GameDataConverter.toJson(game);

            Game game2 = GameDataConverter.fromJson(json);

            return;
        }

        // NORMAL MODE

    }
}