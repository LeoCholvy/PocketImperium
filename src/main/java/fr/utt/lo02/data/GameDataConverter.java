package fr.utt.lo02.data;

import fr.utt.lo02.core.Game;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;


public class GameDataConverter {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    public static String toJson(Game game) {
        return gson.toJson(game);
    }
    public static Game fromJson(String json) {
        Game game = gson.fromJson(json, Game.class);
        game.setNeighbors();
        return game;
    }
}