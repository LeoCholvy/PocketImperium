package fr.utt.lo02.IO;

import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.data.DataManipulator;
import fr.utt.lo02.data.GameDataConverter;

import java.io.*;

public class CLI {
    public static void displayGameState() {
        System.out.println("-----------------------------------------------------------");
        System.out.println(GameDataConverter.toJson(Game.getInstance()));
        System.out.println("-----------------------------------------------------------");
    }
    public static int placeTwoShips(Player player) {
        displayGameState();
        System.out.print("Player " + player.getName() + ", chose a cell to place your ship:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            displayError("Invalid input, please enter a number");
            return placeTwoShips(player);
        }
    }
    public static void displayError(String message) {
        System.out.println("Error: " + message);
    }
}
