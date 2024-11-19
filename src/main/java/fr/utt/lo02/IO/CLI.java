package fr.utt.lo02.IO;

import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.data.GameDataConverter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CLI implements IOHandler {
    public void displayGameState() {
        System.out.println("-----------------------------------------------------------");
        System.out.println(GameDataConverter.toJson(Game.getInstance()));
        System.out.println("-----------------------------------------------------------");
    }
    public int getStartingCellId(int playerid) {
        Player player = Game.getInstance().getPlayer(playerid);
        displayGameState();
        System.out.println("Player " + player.getName() + ", chose a cell to place your ship");
        System.out.println("You must chose a cell with a level 1 system in an unoccupied sector");
        System.out.print(">>>");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return Integer.parseInt(reader.readLine());
        } catch (Exception e) {
            displayError("Invalid input, please enter a number");
            return getStartingCellId(playerid);
        }
    }
    public void displayError(String message) {
        System.out.println("Error: " + message);
    }

    public HashMap<Integer, Command[]> getCommandOrders() {
        HashMap<Integer, Command[]> orders = new HashMap<>();
        // ask each player their order
        for (Player player : Game.getInstance().getAlivePlayers()) {
            System.out.println("Player " + player.getName() + ", enter your command");
            System.out.println("1. Expand");
            System.out.println("2. Explore");
            System.out.println("3. Exterminate");
            System.out.println("input example : 213 for Explore, Expand, Exterminate");
            System.out.print(">>>");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String input = reader.readLine();
                Command[] commands = new Command[3];
                // input need to be 3 characters
                // contains 1,2,3 and no duplicate
                if (input.length() != 3 || !input.contains("1") || !input.contains("2") || !input.contains("3")) {
                    displayError("Invalid input, please enter a valid command");
                    return getCommandOrders();
                }
                for (int i = 0; i < 3; i++) {
                    if (input.charAt(i) == '1') {
                        commands[i] = Command.EXPAND;
                    } else if (input.charAt(i) == '2') {
                        commands[i] = Command.EXPLORE;
                    } else if (input.charAt(i) == '3') {
                        commands[i] = Command.EXTERMINATE;
                    }
                }
                orders.put(player.getId(), commands);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return orders;
    }

    public int[][] expand(int playerId, int nShips) {
        ArrayList<int[]> ships = new ArrayList<>();
        Player player = Game.getInstance().getPlayer(playerId);
        // displayGameState();
        int nShipsMax = Math.min(nShips, player.getNumberAvailableShips());
        System.out.println("Player " + player.getName() + ", you have " + nShipsMax + " ships to expand");
        while (nShipsMax > 0) {
            int nShipsOnCell;
            int cellId;
            displayGameState();
            System.out.println("Player " + player.getName() + ", chose a cell to expand your ship");
            System.out.println("You have " + nShipsMax + " ships left, write -1 to stop expanding");
            System.out.print(">>>");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                cellId = Integer.parseInt(reader.readLine());
            } catch (IOException e) {
                displayError("Invalid input, please enter a number");
                continue;
            }
            if (cellId == -1) {
                break;
            }
            if (nShipsMax == 1) {
                nShipsOnCell = 1;
            } else {
                System.out.println("How many ships do you want to put on this cell?");
                System.out.print(">>>");
                try {
                    nShipsOnCell = Integer.parseInt(reader.readLine());
                } catch (IOException e) {
                    displayError("Invalid input, please enter a number");
                    continue;
                }
                if (!(nShipsOnCell >= 1 && nShipsOnCell <= nShipsMax)) {
                    displayError("Invalid input, please enter a number between 1 and " + nShipsMax);
                    continue;
                }
            }
            ships.add(new int[]{cellId, nShipsOnCell});
            nShipsMax -= nShipsOnCell;
        }
        return ships.toArray(new int[0][0]);
    }

    public int[][][] explore(int playerId, int nFleet) {
        // TODO : INPUT : explore
        return new int[0][0][0];
    }

    public int[][] exterminate(int playerId, int nFleet) {
        // TODO : INPUT : exterminate
        return new int[0][0];
    }

    public int score(int id) {
        return 0;
    }
}
