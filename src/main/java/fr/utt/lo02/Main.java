package fr.utt.lo02;

import fr.utt.lo02.IO.CLI;
import fr.utt.lo02.IO.IOMode;
import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.core.components.System;
import fr.utt.lo02.data.GameDataConverter;

import java.util.Arrays;
import java.util.HashMap;

public class Main {
    public static Game game;
    public static void main(String[] args) {
        IOHandler io = new CLI();

        // DEBUG MODE
        if (args.length >= 1) {
            // DEBUG MODE
            // Game game = GameDataConverter.fromJson("{\"area\":{\"grid\":[{\"id\":0,\"system\":{\"id\":12,\"level\":3}},{\"id\":1,\"system\":{\"id\":0,\"level\":1}},{\"id\":2},{\"id\":3,\"system\":{\"id\":4,\"level\":1}},{\"id\":4,\"system\":{\"id\":5,\"level\":2}},{\"id\":5},{\"id\":6,\"system\":{\"id\":7,\"level\":1}},{\"id\":7},{\"id\":8},{\"id\":9},{\"id\":10},{\"id\":11,\"system\":{\"id\":8,\"level\":2}},{\"id\":12,\"system\":{\"id\":1,\"level\":1}},{\"id\":13,\"system\":{\"id\":2,\"level\":2}},{\"id\":14,\"system\":{\"id\":3,\"level\":1}},{\"id\":15},{\"id\":16},{\"id\":17,\"system\":{\"id\":6,\"level\":1}},{\"id\":18,\"system\":{\"id\":9,\"level\":1}},{\"id\":19},{\"id\":20,\"system\":{\"id\":10,\"level\":1}},{\"id\":21},{\"id\":22,\"system\":{\"id\":11,\"level\":2}},{\"id\":23},{\"id\":24},{\"id\":25},{\"id\":26,\"system\":{\"id\":13,\"level\":1}},{\"id\":27,\"system\":{\"id\":15,\"level\":2}},{\"id\":28},{\"id\":29,\"system\":{\"id\":14,\"level\":1}},{\"id\":30,\"system\":{\"id\":18,\"level\":2}},{\"id\":31,\"system\":{\"id\":17,\"level\":1}},{\"id\":32,\"system\":{\"id\":21,\"level\":2}},{\"id\":33},{\"id\":34},{\"id\":35,\"system\":{\"id\":23,\"level\":1}},{\"id\":36},{\"id\":37},{\"id\":38,\"system\":{\"id\":19,\"level\":1}},{\"id\":39},{\"id\":40,\"system\":{\"id\":22,\"level\":1}},{\"id\":41,\"system\":{\"id\":16,\"level\":1}},{\"id\":42},{\"id\":43},{\"id\":44,\"system\":{\"id\":20,\"level\":1}},{\"id\":45,\"system\":{\"id\":24,\"level\":2}},{\"id\":46}],\"sectors\":[{\"id\":0,\"cellIds\":[1,2,7,12,13]},{\"id\":1,\"cellIds\":[3,4,9,14,15]},{\"id\":2,\"cellIds\":[5,6,11,16,17]},{\"id\":3,\"cellIds\":[18,20,21,22]},{\"id\":4,\"cellIds\":[0]},{\"id\":5,\"cellIds\":[25,26,27,29]},{\"id\":6,\"cellIds\":[30,31,36,41,42]},{\"id\":7,\"cellIds\":[32,33,38,43,44]},{\"id\":8,\"cellIds\":[34,35,40,45,46]}]},\"players\":[{\"id\":0,\"name\":\"Dodo\",\"score\":0,\"ships\":[{\"id\":0,\"cellId\":1},{\"id\":1,\"cellId\":1},{\"id\":2,\"cellId\":26},{\"id\":3,\"cellId\":26},{\"id\":4},{\"id\":5},{\"id\":6},{\"id\":7},{\"id\":8},{\"id\":9},{\"id\":10},{\"id\":11},{\"id\":12},{\"id\":13},{\"id\":14}],\"dead\":false},{\"id\":1,\"name\":\"Leo\",\"score\":0,\"ships\":[{\"id\":15,\"cellId\":3},{\"id\":16,\"cellId\":3},{\"id\":17,\"cellId\":6},{\"id\":18,\"cellId\":6},{\"id\":19},{\"id\":20},{\"id\":21},{\"id\":22},{\"id\":23},{\"id\":24},{\"id\":25},{\"id\":26},{\"id\":27},{\"id\":28},{\"id\":29}],\"dead\":false}],\"startingPlayerIndex\":0,\"round\":0}");
            // game.initIO(io);
            // java.lang.System.out.println(game.playRound());
            // java.lang.System.out.println(GameDataConverter.toJson(game));
            Game game = new Game(io, new Player[]{new Player("Dodo"), new Player("Leo")});
            game.init();
            game.playRound();

            return;
        }

        // NORMAL MODE

    }
}