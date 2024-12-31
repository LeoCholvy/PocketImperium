package fr.utt.lo02.IO;

import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.IllegalGameStateExeceptions;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Area;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.core.components.Sector;
import fr.utt.lo02.data.GameDataConverter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The CLI class implements the IOHandler interface and provides a command-line interface for interacting with the game.
 */
public class CLI implements IOHandler {

    private Game game;

    /**
     * Default constructor.
     * You can use it right after creating the game.
     * Remember: the game needs to be initialized with the IOHandler before starting!
     * @see Game#initIO(IOHandler)
     */
    public CLI() {
        this.game = Game.getInstance();
        if (this.game == null) {
            throw new IllegalGameStateExeceptions("Game not found");
        }
    }

    /**
     * Constructs a new CLI instance with the specified game.
     *
     * @param game the game instance
     */
    public CLI(Game game) {
        this.game = game;
    }

    /**
     * Sets the game instance.
     *
     * @param game the game instance
     */
    public void setGameInstance(Game game) {
        this.game = game;
        if (this.game == null) {
            throw new IllegalGameStateExeceptions("Error while setting the game instance");
        }
    }

    /**
     * Displays the current game state in JSON format.
     */
    public void displayGameState() {
        System.out.println("-----------------------------------------------------------");
        System.out.println(GameDataConverter.toJson(this.game));
        System.out.println("-----------------------------------------------------------");
    }

    /**
     * Prompts the player to choose a starting cell for their ship.
     *
     * @param playerid the ID of the player
     * @return the ID of the chosen starting cell
     */
    public int getStartingCellId(int playerid) {
        Player player = this.game.getPlayer(playerid);
        displayGameState();
        System.out.println("Player " + player.getName() + ", chose a cell to place your ship");
        System.out.println("You must chose a cell with a level 1 system in an unoccupied sector");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print(">>>");
            return Integer.parseInt(reader.readLine());
        } catch (Exception e) {
            displayError("Invalid input, please enter a number");
            return getStartingCellId(playerid);
        }
    }

    /**
     * Displays an error message for a specific player.
     *
     * @param message the error message
     * @param playerId the ID of the player
     */
    public void displayError(String message, int playerId) {
        System.out.println("\u001B[31mError: " + message + "\u001B[0m");
    }

    /**
     * Displays an error message.
     *
     * @param message the error message
     */
    public void displayError(String message) {
        System.out.println("\u001B[31mError: " + message + "\u001B[0m");
    }

    /**
     * Prompts each player to enter their command orders.
     *
     * @return a map of player IDs to their chosen command orders
     */
    public HashMap<Integer, Command[]> getCommandOrders() {
        HashMap<Integer, Command[]> orders = new HashMap<>();
        for (Player player : this.game.getAlivePlayers()) {
            System.out.println("Player " + player.getName() + ", enter your command");
            System.out.println("1. Expand");
            System.out.println("2. Explore");
            System.out.println("3. Exterminate");
            System.out.println("input example : 213 for Explore, Expand, Exterminate");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.print(">>>");
                String input = reader.readLine();
                Command[] commands = new Command[3];
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
            } catch (Exception e) {
                displayError("Invalid input, please enter a valid command");
                return getCommandOrders();
            }
        }
        return orders;
    }

    /**
     * Prompts the player to choose cells to expand their ships to.
     *
     * @param playerId the ID of the player
     * @param nShips the number of ships to expand
     * @return a 2D array where each sub-array contains the cell ID and the number of ships placed on that cell
     */
    public int[][] expand(int playerId, int nShips) {
        ArrayList<int[]> ships = new ArrayList<>();
        Player player = this.game.getPlayer(playerId);
        int nShipsMax = Math.min(nShips, player.getNumberAvailableShips());
        System.out.println("Player " + player.getName() + ", you have " + nShipsMax + " ships to expand");
        while (nShipsMax > 0) {
            int nShipsOnCell;
            int cellId;
            displayGameState();
            System.out.println("Player " + player.getName() + ", chose a cell to expand your ship");
            System.out.println("You have " + nShipsMax + " ships left, write -1 to stop expanding");
            System.out.println("Choose a cell to expand");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.print(">>>");
                cellId = Integer.parseInt(reader.readLine());
            } catch (Exception e) {
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

    /**
     * Prompts the player to choose cells to explore with their fleet.
     *
     * @param playerId the ID of the player
     * @param nFleet the number of fleets to explore with
     * @return a 3D array where each sub-array contains the details of the exploration
     */
    public int[][][] explore(int playerId, int nFleet) {
        List<List<int[]>> input = new ArrayList<>();
        Player player = this.game.getPlayer(playerId);
        displayGameState();
        Area area = this.game.getArea();
        System.out.println("Player " + player.getName() + ", you have " + nFleet + " you can move");
        for (int i = 0; i < nFleet; i++) {
            List<int[]> fleet = new ArrayList<>();
            System.out.println(i + "Choose the starting cell for the fleet (write -1 to stop moving)");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int startCellId, nShips, destCellId;
            while (true) {
                try {
                    System.out.print(i + ">>>");
                    startCellId = Integer.parseInt(reader.readLine());
                    if (startCellId == -1) {
                        break;
                    }
                    if (area.getCell(startCellId).getOwner() != player) {
                        throw new IllegalGameStateExeceptions("You don't own this cell");
                    }
                    break;
                } catch (Exception e) {
                    displayError("Invalid input, please enter a number");
                    continue;
                }
            }
            if (startCellId == -1) {
                break;
            }
            System.out.println(i + "How many ships do you want to move ? (max " + area.getCell(startCellId).getShips().length + ")");
            while (true) {
                try {
                    System.out.print(i + ">>>");
                    nShips = Integer.parseInt(reader.readLine());
                    if (area.getCell(startCellId).getAvailableShips(nShips) == null) {
                        throw new IllegalGameStateExeceptions("You don't have enough ships on this cell");
                    }
                    break;
                } catch (IllegalGameStateExeceptions e) {
                    displayError(e.getMessage());
                    continue;
                } catch (Exception e) {
                    displayError("Invalid input, please enter a number");
                    continue;
                }
            }
            System.out.println(i + "Choose the destination cell for the fleet");
            while (true) {
                try {
                    System.out.print(i + ">>>");
                    destCellId = Integer.parseInt(reader.readLine());
                    if (destCellId == -1) {
                        break;
                    }
                    if (area.getCell(destCellId).getOwner() != player && area.getCell(destCellId).getOwner() != null) {
                        throw new IllegalGameStateExeceptions("This cell is already owned by another player");
                    }
                    Integer distance = area.getCell(startCellId).distance(area.getCell(destCellId), 2);
                    if (distance == null || distance >= 2) {
                        throw new IllegalGameStateExeceptions("The destination cell is not a neighbor of the starting cell");
                    }
                    break;
                } catch (IllegalGameStateExeceptions e) {
                    displayError(e.getMessage());
                    continue;
                } catch (Exception e) {
                    displayError("Invalid input, please enter a number");
                    continue;
                }
            }
            if (destCellId == -1) {
                break;
            }
            fleet.add(new int[]{startCellId, nShips, destCellId});

            System.out.println(i + "Do you want to move the fleet further ? (y/n)");
            String response;
            try {
                System.out.print(i + ">>>");
                response = reader.readLine();
            } catch (Exception e) {
                displayError("Invalid input, please enter a number");
                continue;
            }
            int nShips2, destCellId2;
            if (response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes")) {
                System.out.println(i + "How many ships do you want to add to your fleet ? (max " + area.getCell(destCellId).getShips().length + ")");
                while (true) {
                    try {
                        System.out.print(i + ">>>");
                        nShips2 = Integer.parseInt(reader.readLine());
                        if (area.getCell(destCellId).getAvailableShips(nShips2) == null) {
                            throw new IllegalGameStateExeceptions("You don't have enough ships on this cell");
                        }
                        break;
                    } catch (IllegalGameStateExeceptions e) {
                        displayError(e.getMessage());
                        continue;
                    } catch (Exception e) {
                        displayError("Invalid input, please enter a number");
                        continue;
                    }
                }
                System.out.println(i + "Choose the destination cell for the fleet");
                while (true) {
                    try {
                        System.out.print(i + ">>>");
                        destCellId2 = Integer.parseInt(reader.readLine());
                        if (destCellId2 == -1) {
                            break;
                        }
                        if (area.getCell(destCellId2).getOwner() != player && area.getCell(destCellId2).getOwner() != null) {
                            throw new IllegalGameStateExeceptions("This cell is already owned by another player");
                        }
                        Integer distance = area.getCell(destCellId).distance(area.getCell(destCellId2), 2);
                        if (distance == null || distance >= 2) {
                            throw new IllegalGameStateExeceptions("The destination cell is not a neighbor of the starting cell");
                        }
                        break;
                    } catch (IllegalGameStateExeceptions e) {
                        displayError(e.getMessage());
                        continue;
                    } catch (Exception e) {
                        displayError("Invalid input, please enter a number");
                        continue;
                    }
                }
                if (destCellId2 == -1) {
                    break;
                }
                fleet.add(new int[]{destCellId, nShips2, destCellId2});
            }
            input.add(fleet);
        }
        int[][][] result = new int[input.size()][][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).toArray(new int[0][0]);
        }
        return result;
    }

    /**
     * Prompts the player to choose cells to exterminate enemy ships.
     *
     * @param playerId the ID of the player
     * @param nSystem the number of systems to exterminate
     * @return a 2D array where each sub-array contains the details of the extermination
     */
    public int[][] exterminate(int playerId, int nSystem) {
        List<List<Integer>> input = new ArrayList<>();
        Game game = this.game;
        Player player = game.getPlayer(playerId);
        Area area = game.getArea();
        displayGameState();
        System.out.println("Player " + player.getName() + ", you have " + nSystem + " you can exterminate");
        for (int i = 0; i < nSystem; i++) {
            List<Integer> currentAttack = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(i + "Player " + player.getName() + ", chose a cell to exterminate");
            System.out.println(i + "You have " + nSystem + " attacks left, write -1 to stop");
            System.out.println(i + "Remember: you can't use the same ship twice in a round, and you can only exterminate cells once per round");
            int cellId, id, nShips, max;
            while (true) {
                try {
                    System.out.print(i + ">>>");
                    cellId = Integer.parseInt(reader.readLine());
                    if (cellId == -1) {
                        break;
                    }
                    int finalCellId = cellId;
                    if (input.stream().anyMatch(list -> list.get(0) == finalCellId)) {
                        throw new IllegalGameStateExeceptions("You can't exterminate the same cell twice in a round");
                    }
                    if (area.getCell(cellId).getOwner() == player || area.getCell(cellId).getOwner() == null) {
                        throw new IllegalGameStateExeceptions("You can't exterminate this cell, it needs to be controlled by another player");
                    }
                    break;
                } catch (IllegalGameStateExeceptions e) {
                    displayError(e.getMessage());
                    continue;
                } catch (Exception e) {
                    displayError("Invalid input, please enter a number");
                    continue;
                }
            }
            if (cellId == -1) {
                break;
            }
            while (true) {
                System.out.println(i + "Choose a cell from where you want to attack");
                try {
                    System.out.print(i + ">>>");
                    id = Integer.parseInt(reader.readLine());
                    if (id == -1) {
                        break;
                    }
                    Integer distance = area.getCell(cellId).distance(area.getCell(id), 2);
                    if (distance == null || distance >= 2) {
                        throw new IllegalGameStateExeceptions("The destination cell is not a neighbor of the starting cell");
                    }
                    if (area.getCell(id).getOwner() != player) {
                        throw new IllegalGameStateExeceptions("You don't have ship on this cell");
                    }
                } catch (IllegalGameStateExeceptions e) {
                    displayError(e.getMessage());
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    displayError("Invalid input, please enter a number");
                    continue;
                }

                max = area.getCell(id).getShips().length;
                System.out.println(i + "How many ships do you want to use for this attack? (max " + max + ")");
                try {
                    System.out.print(i + ">>>");
                    nShips = Integer.parseInt(reader.readLine());
                    if (nShips == -1) {
                        break;
                    }
                    if (area.getCell(id).getAvailableShips(nShips) == null) {
                        throw new IllegalGameStateExeceptions("You don't have enough ships on this cell");
                    }
                } catch (IllegalGameStateExeceptions e) {
                    displayError(e.getMessage());
                    continue;
                } catch (Exception e) {
                    displayError("Invalid input, please enter a number");
                    continue;
                }
                if (currentAttack.size() == 0) {
                    currentAttack.add(cellId);
                }
                currentAttack.add(id);
                currentAttack.add(nShips);
            }
            input.add(currentAttack);
        }
        int[][] result = new int[input.size()][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).stream().mapToInt(Integer::intValue).toArray();
        }
        return result;
    }
    /**
     * Prompts the player to choose a sector to score.
     *
     * @param id the ID of the player
     * @return the ID of the scored sector
     */
    public int score(int id) {
        List<Sector> scorableSectors = this.game.getScorablesSectors();
        System.out.println("Scorable sectors : ");
        for (Sector sector : scorableSectors) {
            System.out.println("Id: " + sector.getId() + ", type: " + sector.getType());
        }
        System.out.println("Player " + this.game.getPlayer(id).getName() + ", chose a sector to score");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int sectorId;
        try {
            System.out.print(">>>");
            sectorId = Integer.parseInt(reader.readLine());
            boolean found = false;
            for (Sector sector : scorableSectors) {
                if (sector.getId() == sectorId) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalGameStateExeceptions("This sector is not scorable");
            }
        } catch (IllegalGameStateExeceptions e) {
            displayError(e.getMessage());
            return score(id);
        } catch (Exception e) {
            displayError("Invalid input, please enter a number");
            return score(id);
        }
        return sectorId;
    }

    /**
     * Displays the winners of the game.
     *
     * @param winnersIds an array of player IDs representing the winners
     */
    public void displayWinner(int[] winnersIds) {
        System.out.println("---------------------Congratulations !---------------------");
        for (int id : winnersIds) {
            System.out.println("Player " + this.game.getPlayer(id).getName() + " win !");
        }
        displayScore();
    }

    /**
     * Displays a draw message.
     */
    public void displayDraw() {
        System.out.println("---------------------Congratulations !---------------------");
        System.out.println("It's a draw !");
        displayScore();
    }

    /**
     * Displays the scoreboard with the scores of all players.
     */
    private void displayScore() {
        System.out.println("-----------------------Scoreboard--------------------------");
        for (Player player : this.game.getPlayers()) {
            System.out.println("Player " + player.getName() + " : " + player.getScore());
        }
        System.out.println("-----------------------------------------------------------");
    }
}
