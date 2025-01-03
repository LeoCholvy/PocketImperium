package fr.utt.lo02.data;

import fr.utt.lo02.IO.IA;
import fr.utt.lo02.core.Game;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import fr.utt.lo02.core.Player;

/**
 * The GameDataConverter class provides methods to convert Game objects to and from JSON format.
 */
public class GameDataConverter {
    // private static Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    /**
     * Converts a Game object to its JSON representation.
     *
     * @param game the Game object to convert
     * @return the JSON representation of the Game object
     */
    public static String toJson(Game game) {
        return gson.toJson(game);
    }

    /**
     * Converts a JSON string to a Game object and initializes the game instance.
     *
     * @param json the JSON string representing the Game object
     * @param name the name to set for the Game object
     * @return the Game object created from the JSON string
     */
    public static Game fromJson(String json, String name) {
        Game game = gson.fromJson(json, Game.class);
        Game.setInstance(game); // VERY IMPORTANT
        // the following methods need to get the instance of the game from Game.getInstance()
        game.initNeighbors();
        game.initShipsCells();
        game.initSectorsCells();
        game.initPlayerIterator();
        game.setName(name);

        //init the IA
        IA.setGameInstance(game);
        for (Player player : game.getPlayers()) {
            if (!player.isHuman()) {
                player.initIO(new IA());
            }
        }
        return game;
    }


    public static Game copy(Game gameOriginal) {
        String json = toJson(gameOriginal);


        Game game = gson.fromJson(json, Game.class);
        Game.setInstance(game); // FIXME: this is not the right way to do it
        // the following methods need to get the instance of the game from Game.getInstance()
        game.initNeighbors();
        game.initShipsCells();
        game.initSectorsCells();
        Game.setInstance(gameOriginal);//FIXME : this is not the right way to do it

        return game;
    }
}