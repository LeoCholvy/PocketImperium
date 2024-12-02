package fr.utt.lo02.IO;

import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.IllegalGameStateExeceptions;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Area;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.core.components.Sector;
import fr.utt.lo02.data.GameDataConverter;

import java.io.*;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class CLI implements IOHandler {

    private Game game;

    /**
     * Default constructor
     * You can use it right after creating the game
     * Remember : the game need to be initialized with the IOHandler before starting !
     * @see Game#initIO(IOHandler)
     */
    public CLI() {
        this.game = null;
    }
    public CLI(Game game) {
        this.game = game;
    }
    public void setGameInstance(Game game) {
        this.game = game;
        if (this.game == null) {
            throw new IllegalGameStateExeceptions("Error while setting the game instance");
        }
    }

    public void displayGameState() {
        System.out.println("-----------------------------------------------------------");
        System.out.println(GameDataConverter.toJson(this.game));
        System.out.println("-----------------------------------------------------------");
    }
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
    public void displayError(String message) {
        System.out.println("Error: " + message);
    }

    public HashMap<Integer, Command[]> getCommandOrders() {
        HashMap<Integer, Command[]> orders = new HashMap<>();
        // ask each player their order
        for (Player player : this.game.getAlivePlayers()) {
            // TODO : check if the player is human


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
                // input need to be 3 characters
                // contains 1,2,3 and no duplicate
                if (input.length() != 3 || !input.contains("1") || !input.contains("2") || !input.contains("3")) {
                    displayError("Invalid input, please enter a valid command");
                    // FIXME : need to ask the player again and not every player
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

    public int[][] expand(int playerId, int nShips) {
        ArrayList<int[]> ships = new ArrayList<>();
        Player player = this.game.getPlayer(playerId);
        // displayGameState();
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

    public int[][][] explore(int playerId, int nFleet) {
        // FIXME : let the player choose to do nothing with -1
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
            // FIXME : not the right way to do it, we need to check if the ship is used !!!!
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
                    if (area.getCell(startCellId).distance(area.getCell(destCellId), 2) != 1) {
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

            System.out.println(i+"Do you want to move the fleet further ? (y/n)");
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
                System.out.println(i+"How many ships do you want to add to your fleet ? (max " + area.getCell(destCellId).getShips().length + ")");
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
                System.out.println(i+"Choose the destination cell for the fleet");
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
                        if (area.getCell(destCellId).distance(area.getCell(destCellId2), 2) != 1) {
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
                fleet.add(new int[]{destCellId,nShips2, destCellId2});
            }
            input.add(fleet);

            // System.out.println("Do you want to move another fleet ? (y/n)");
            // try {
            //     System.out.print(">>>");
            //     response = reader.readLine();
            // } catch (Exception e) {
            //     displayError("Invalid input, please enter a number");
            //     continue;
            // }
            // if (response.equalsIgnoreCase("n") || response.equalsIgnoreCase("no")) {
            //     break;
            // }
        }
        int [][][] result = new int[input.size()][][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).toArray(new int[0][0]);
        }
        return result;
    }

    public int[][] exterminate(int playerId, int nSystem) {
        // TODO : INPUT : exterminate
        List<List<Integer>> input = new ArrayList<>();
        Game game = this.game;
        Player player = game.getPlayer(playerId);
        Area area = game.getArea();
        displayGameState();
        System.out.println("Player " + player.getName() + ", you have " + nSystem + " you can exterminate");
        for (int i = 0; i < nSystem; i++) {
            List<Integer> currentAttack = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(i+"Player " + player.getName() + ", chose a cell to exterminate");
            System.out.println(i+"You have " + nSystem + " attacks left, write -1 to stop");
            System.out.println(i+"Remember : you can't use the same ship twice in a round, and you can only exterminate cells once per round");
            int cellId, id, nShips, max;
            while (true) {
                try {
                    System.out.print(i + ">>>");
                    cellId = Integer.parseInt(reader.readLine());
                    if (cellId == -1) {
                        break;
                    }
                    // check all the i[0] of input and if on of them is equal to cellId, throw an error
                    int finalCellId = cellId;
                    if (input.stream().anyMatch(list -> list.get(0) == finalCellId)) {
                        throw new IllegalGameStateExeceptions("You can't exterminate the same cell twice in a round");
                    }
                    if (area.getCell(cellId).getOwner() == player || area.getCell(cellId).getOwner() == null) {
                        throw new IllegalGameStateExeceptions("You can't exterminate this cell, it need to be controled by another player");
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
                System.out.println(i+"Choose a cell from where you want to attack");
                try {
                    System.out.print(i + ">>>");
                    id = Integer.parseInt(reader.readLine());
                    if (id == -1) {
                        break;
                    }
                    if (area.getCell(cellId).distance(area.getCell(id), 2) == null) {
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
                System.out.println(i+"How many ships do you want to use for this attack ? (max " + max + ")");
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
        // convert list<list<int>> to int[][]
        int[][] result = new int[input.size()][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).stream().mapToInt(Integer::intValue).toArray();
        }
        return result;
    }

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

    public void displayWinner(int[] winnersIds) {
        System.out.println("---------------------Congratulations !---------------------");
        for (int id : winnersIds) {
            System.out.println("Player " + this.game.getPlayer(id).getName() + " win !");
        }
        // System.out.println("-----------------------------------------------------------");
        displayScore();
    }

    public void displayDraw() {
        System.out.println("---------------------Congratulations !---------------------");
        System.out.println("It's a draw !");
        // System.out.println("-----------------------------------------------------------");
        displayScore();
    }

    private void displayScore() {
        System.out.println("-----------------------Scoreboard--------------------------");
        for (Player player : this.game.getPlayers()) {
            System.out.println("Player " + player.getName() + " : " + player.getScore());
        }
        System.out.println("-----------------------------------------------------------");
    }
}
