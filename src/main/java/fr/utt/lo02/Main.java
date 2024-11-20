package fr.utt.lo02;

import fr.utt.lo02.IO.CLI;
import fr.utt.lo02.IO.IOMode;
import fr.utt.lo02.IO.IOHandler;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.core.components.Ship;
import fr.utt.lo02.core.components.System;
import fr.utt.lo02.data.GameDataConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
            // Game game = new Game(io, new Player[]{new Player("Dodo"), new Player("Leo")});
            // Game game = Game.getInstance(io, new Player[]{new Player("Dodo", 0), new Player("Leo", 1)});
            // game.init();
            // game.playRound();

            Game game = Game.getInstance("{\"area\":{\"grid\":[{\"id\":0,\"system\":{\"id\":12,\"level\":3},\"neighborIds\":[14,15,19,24,21,26,23,28,32,33]},{\"id\":1,\"neighborIds\":[2,7]},{\"id\":2,\"neighborIds\":[1,3,7,8]},{\"id\":3,\"neighborIds\":[2,4,8,9]},{\"id\":4,\"neighborIds\":[3,5,9,10]},{\"id\":5,\"neighborIds\":[4,6,10,11]},{\"id\":6,\"system\":{\"id\":8,\"level\":2},\"neighborIds\":[5,11]},{\"id\":7,\"system\":{\"id\":2,\"level\":2},\"neighborIds\":[1,2,8,12,13]},{\"id\":8,\"neighborIds\":[2,3,7,9,13,14]},{\"id\":9,\"system\":{\"id\":3,\"level\":1},\"neighborIds\":[3,4,8,10,14,15]},{\"id\":10,\"neighborIds\":[4,5,9,11,15,16]},{\"id\":11,\"system\":{\"id\":6,\"level\":1},\"neighborIds\":[5,6,10,16,17]},{\"id\":12,\"system\":{\"id\":1,\"level\":1},\"neighborIds\":[7,13,18]},{\"id\":13,\"system\":{\"id\":0,\"level\":1},\"neighborIds\":[7,8,12,14,18,19]},{\"id\":14,\"system\":{\"id\":4,\"level\":1},\"neighborIds\":[8,9,13,15,19,0]},{\"id\":15,\"system\":{\"id\":5,\"level\":2},\"neighborIds\":[9,10,14,16,0,24]},{\"id\":16,\"system\":{\"id\":7,\"level\":1},\"neighborIds\":[10,11,15,17,24,25]},{\"id\":17,\"neighborIds\":[11,16,25]},{\"id\":18,\"neighborIds\":[12,13,19,20,21]},{\"id\":19,\"neighborIds\":[13,14,18,0,21]},{\"id\":20,\"system\":{\"id\":11,\"level\":2},\"neighborIds\":[18,21,22]},{\"id\":21,\"system\":{\"id\":10,\"level\":1},\"neighborIds\":[18,19,20,22,23,0]},{\"id\":22,\"system\":{\"id\":9,\"level\":1},\"neighborIds\":[20,21,23,30,31]},{\"id\":23,\"neighborIds\":[21,22,31,32,0]},{\"id\":24,\"neighborIds\":[15,16,25,26,0]},{\"id\":25,\"system\":{\"id\":13,\"level\":1},\"neighborIds\":[16,17,24,26,27]},{\"id\":26,\"system\":{\"id\":14,\"level\":1},\"neighborIds\":[24,25,27,28,0,29]},{\"id\":27,\"system\":{\"id\":15,\"level\":2},\"neighborIds\":[25,26,29]},{\"id\":28,\"neighborIds\":[26,29,33,34,0]},{\"id\":29,\"neighborIds\":[26,27,28,34,35]},{\"id\":30,\"system\":{\"id\":18,\"level\":2},\"neighborIds\":[22,31,36]},{\"id\":31,\"system\":{\"id\":17,\"level\":1},\"neighborIds\":[22,23,30,32,36,37]},{\"id\":32,\"neighborIds\":[23,31,33,37,38,0]},{\"id\":33,\"neighborIds\":[0,28,32,38,39]},{\"id\":34,\"neighborIds\":[28,29,33,35,39,40]},{\"id\":35,\"system\":{\"id\":24,\"level\":2},\"neighborIds\":[29,34,40]},{\"id\":36,\"neighborIds\":[30,31,37,41,42]},{\"id\":37,\"neighborIds\":[31,32,36,38,42,43]},{\"id\":38,\"system\":{\"id\":19,\"level\":1},\"neighborIds\":[32,33,37,39,43,44]},{\"id\":39,\"neighborIds\":[33,34,38,40,44,45]},{\"id\":40,\"system\":{\"id\":22,\"level\":1},\"neighborIds\":[34,35,39,45,46]},{\"id\":41,\"neighborIds\":[36,42]},{\"id\":42,\"system\":{\"id\":16,\"level\":1},\"neighborIds\":[36,37,41,43]},{\"id\":43,\"system\":{\"id\":21,\"level\":2},\"neighborIds\":[37,38,42,44]},{\"id\":44,\"system\":{\"id\":20,\"level\":1},\"neighborIds\":[38,39,43,45]},{\"id\":45,\"neighborIds\":[39,40,44,46]},{\"id\":46,\"system\":{\"id\":23,\"level\":1},\"neighborIds\":[40,45]}],\"sectors\":[{\"id\":0,\"cellIds\":[1,2,7,12,13],\"type\":\"BORDER\"},{\"id\":1,\"cellIds\":[3,4,9,14,15],\"type\":\"BORDER\"},{\"id\":2,\"cellIds\":[5,6,11,16,17],\"type\":\"BORDER\"},{\"id\":3,\"cellIds\":[18,20,21,22],\"type\":\"MIDDLE\"},{\"id\":4,\"cellIds\":[0],\"type\":\"TRI_PRIME\"},{\"id\":5,\"cellIds\":[25,26,27,29],\"type\":\"MIDDLE\"},{\"id\":6,\"cellIds\":[30,31,36,41,42],\"type\":\"BORDER\"},{\"id\":7,\"cellIds\":[32,33,38,43,44],\"type\":\"BORDER\"},{\"id\":8,\"cellIds\":[34,35,40,45,46],\"type\":\"BORDER\"}]},\"players\":[{\"id\":0,\"name\":\"Dodo\",\"score\":0,\"ships\":[{\"id\":0,\"cellId\":31},{\"id\":1,\"cellId\":31},{\"id\":2,\"cellId\":44},{\"id\":3,\"cellId\":44},{\"id\":4},{\"id\":5},{\"id\":6},{\"id\":7},{\"id\":8},{\"id\":9},{\"id\":10},{\"id\":11},{\"id\":12},{\"id\":13},{\"id\":14}],\"dead\":false},{\"id\":1,\"name\":\"Leo\",\"score\":0,\"ships\":[{\"id\":0,\"cellId\":11},{\"id\":1,\"cellId\":11},{\"id\":2,\"cellId\":46},{\"id\":3,\"cellId\":46},{\"id\":4},{\"id\":5},{\"id\":6},{\"id\":7},{\"id\":8},{\"id\":9},{\"id\":10},{\"id\":11},{\"id\":12},{\"id\":13},{\"id\":14}],\"dead\":false}],\"startingPlayerIndex\":1,\"round\":0}");
            game.initIO(io);
            // Ship ships[] = game.getPlayer(0).getShips();
            // for (Ship ship : ships) {
            //     java.lang.System.out.println(ship.getCell().getId());
            // }
            // java.lang.System.out.println(GameDataConverter.toJson(game));
            // game.getPlayer(0).getAvailableShips(1).forEach(ship -> ship.setCell(game.getArea().getCell(16)));
            List.of(game.getPlayer(1).getAvailableShips(1)).forEach(ship -> ship.setCell(game.getArea().getCell(16)));
            List.of(game.getPlayer(0).getAvailableShips(3)).forEach(ship -> ship.setCell(game.getArea().getCell(17)));
            game.playRound();

            // Game game = Game.getInstance(io, new Player[]{new Player("Dodo", 0), new Player("Leo", 1)});
            // game.init();
            // java.lang.System.out.println(GameDataConverter.toJson(game));

            return;
        }

        // NORMAL MODE

    }
}