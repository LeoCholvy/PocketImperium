package fr.utt.lo02.data;

import fr.utt.lo02.core.Game;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public class GameDataConverter {
    // private static Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    public static String toJson(Game game) {
        return gson.toJson(game);
    }
    public static Game fromJson(String json) {
        // FIXME : also add IOHandler to the game
        Game game = gson.fromJson(json, Game.class);
        Game.setInstance(game); // VERY IMPORTANT
        // the following methods need to get the instance of the game from Game.getInstance()
        game.initNeighbors();
        game.initShipsCells();
        game.initSectorsCells();
        game.initPlayerIterator();
        return game;
    }
}